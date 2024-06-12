package pj.domain

import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.{OTime, Preference}
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference.Preference
import pj.xml.XML

import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec
import scala.collection.immutable.HashSet
import scala.xml.Node


final case class Availability(
                               start: OTime,
                               end: OTime,
                               preference: Preference
                             )

object Availability:
  def from(start: OTime, end: OTime, preference: Preference) =
    new Availability(start: OTime, end: OTime, preference: Preference)


  def fromCheck(start: OTime, end: OTime, preference: Preference): Result[Availability] =
    if start.isBefore(end) then Right(Availability.from(start, end, preference))
    else Left(DomainError.WrongFormat("End time must be after start time"))

  extension (a: Availability)
    def isValid: Boolean = a.start.isValid && a.end.isValid && a.preference.isValid && a.start.isBefore(a.end)

  def createLocal(start: LocalDateTime, end: LocalDateTime, preference: Int): Availability =
    val res = for {
      start <- OTime.createTime(start)
      end <- OTime.createTime(end)
      resPreference = Preference.fromMoreThan5(preference)
    } yield Availability.from(start, end, resPreference)
    res match
      case Right(value) => value


  def intersects(availability1: Availability, availability2: Availability): Boolean =
    (availability1.start.isBefore(availability2.end) && availability1.end.isAfter(availability2.start)) ||
      (availability2.start.isBefore(availability1.end) && availability2.end.isAfter(availability1.start))

  def findAllPossibleAvailabilitiesSlot(availabilities: List[List[Availability]], duration: ODuration): List[Availability] =
    @tailrec
    def combine(availabilities: List[List[Availability]], acc: List[Availability]): List[Availability] =
      availabilities match
        case Nil => acc
        case head :: tail =>
          val combined = for {
            a <- head
            b <- acc
            start = if (a.start.isAfter(b.start)) a.start else b.start
            end = if (a.end.isBefore(b.end)) a.end else b.end
            if start.isBefore(end) && Duration.between(start.toTemporal, end.toTemporal).compareTo(duration.toDuration) >= 0
          } yield Availability.from(start, end, Preference.fromMoreThan5(a.preference + b.preference))
          combine(tail, combined)

    combine(availabilities.drop(1), availabilities.headOption.getOrElse(List.empty)).sortBy(_.start)

  def chooseFirstPossibleAvailability(availabilities: List[Availability], duration: ODuration, usedSlots: List[(HashSet[ID], Availability)], newIds: HashSet[ID]): (Option[(LocalDateTime, LocalDateTime, Int)], List[(HashSet[ID], Availability)]) =
    availabilities.headOption match
      case Some(slot) =>
        val start = slot.start.toLocalDateTime
        val end = start.plus(duration.toDuration)
        val availEnd = OTime.createTime(end).getOrElse(slot.end)
        (Some((start, end, slot.preference.toInteger)), (newIds, Availability.from(slot.start, availEnd, slot.preference)) :: usedSlots)
      case None =>
        (None, usedSlots)

  def chooseFirstPossibleAvailabilitiesSlot(availabilities: List[Availability], duration: ODuration, usedSlots: List[Availability]): (Option[(LocalDateTime, LocalDateTime, Int)], List[Availability]) =
    val availableSlots = availabilities.filterNot(usedSlots.contains)
    availableSlots.headOption match
      case Some(slot) =>
        val start = slot.start.toLocalDateTime
        val end = start.plus(duration.toDuration)
        val availEnd = OTime.createTime(end).getOrElse(slot.end)
        (Some((start, end, slot.preference.toInteger)), Availability.from(slot.start, availEnd, slot.preference) :: usedSlots)
      case None =>
        (None, usedSlots)

  def updateVivasBasedOnUsedSlots(preViva: PreViva, usedSlots: List[(HashSet[ID], Availability)], newIds: HashSet[ID], duration: ODuration): List[List[Availability]] =
    preViva.roleLinkedWithResourceList.map { roleLinkedWithResource =>
      val roleId = roleLinkedWithResource.getRoleId
      val availList = roleLinkedWithResource.listAvailability

      val updatedAvailList = usedSlots.foldLeft(availList) { (currentAvailList, usedSlot) =>
        val (_, usedSlotAvailability) = usedSlot
        if (usedSlot._1.contains(roleId))
          updateAvailabilitySlots2(currentAvailList, duration, List(usedSlotAvailability))
        else
          currentAvailList
      }

      updatedAvailList
    }


  def updateAvailabilitySlots(availabilities: List[Availability], duration: ODuration, usedSlots: List[Availability]): List[Availability] =
    val durationBetween: (OTime, OTime) => Boolean = (start, end) =>
      Duration.between(start.toTemporal, end.toTemporal).compareTo(duration.toDuration) >= 0

    availabilities.flatMap { possibleSlot =>
      usedSlots.foldLeft(List(possibleSlot)) { (updatedSlots, usedSlot) =>
        updatedSlots.flatMap { slot =>
          if slot.start.isBefore(usedSlot.end) && slot.end.isAfter(usedSlot.start) then
            if slot.start.isBefore(usedSlot.start) && slot.end.isAfter(usedSlot.end) then
              // Slot overlaps both start and end of usedSlot, split into two
              List(
                slot.copy(end = usedSlot.start),
                slot.copy(start = usedSlot.end)
              ).filter(s => durationBetween(s.start, s.end))
            else if slot.start.isBefore(usedSlot.end) && slot.end.isAfter(usedSlot.end) then
              // Slot overlaps end of usedSlot, adjust start
              val newSlot = slot.copy(start = usedSlot.end)
              if durationBetween(newSlot.start, newSlot.end) then List(newSlot) else Nil
            else if slot.start.isBefore(usedSlot.start) && slot.end.isAfter(usedSlot.start) then
              // Slot overlaps start of usedSlot, adjust end
              val newSlot = slot.copy(end = usedSlot.start)
              if durationBetween(newSlot.start, newSlot.end) then List(newSlot) else Nil
            else Nil
          else if slot.end.equals(usedSlot.start) || slot.start.equals(usedSlot.end) then
            // Handle special case where the slot ends exactly when the used slot starts or vice versa
            List(slot)
          else List(slot)
        }
      }
    }




