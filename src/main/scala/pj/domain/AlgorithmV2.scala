package pj.domain


import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.{OTime, Preference}
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference.Preference

import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec


object AlgorithmV2:
  def makeTheAlgorithmHappen(agenda: Agenda): Result[Unit] =
    val teacherList = agenda.resources.teacher
    val externalList = agenda.resources.external
    val duration = agenda.duration

    val preVivaList = agenda.vivas.map(PreViva.linkVivaWithResource(_, teacherList, externalList))


    def preVivaToMap(vivaList: Seq[PreViva]): Map[Set[President | Advisor | Supervisor | CoAdvisor], List[List[Availability]]] =
      val roles = vivaList.map(viva => viva.roleLinkedWithResourceList.map(_.role).toSet)
      val availabilities = vivaList.map(viva => viva.roleLinkedWithResourceList.map(_.listAvailability))
      roles.zip(availabilities).toMap

    val availabilityMap = preVivaToMap(preVivaList)

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

    def updateAvailabilitySlots(availabilities: List[Availability], duration: ODuration, usedSlots: List[Availability]): List[Availability] =
      availabilities.flatMap { possibleSlot =>
        usedSlots.foldLeft(List(possibleSlot)) { (updatedSlots, usedSlot) =>
          updatedSlots.flatMap { slot =>
            if slot.start.isBefore(usedSlot.end) && slot.end.isAfter(usedSlot.start) then
              if slot.start.isBefore(usedSlot.start) && slot.end.isAfter(usedSlot.end) then
                // Slot overlaps both start and end of usedSlot, split into two
                val newSlots = List(
                  slot.copy(end = usedSlot.start),
                  slot.copy(start = usedSlot.end)
                ).filter(s => Duration.between(s.start.toTemporal, s.end.toTemporal).compareTo(duration.toDuration) >= 0)
                newSlots
              else if slot.start.isBefore(usedSlot.end) && slot.end.isAfter(usedSlot.end) then
                // Slot overlaps end of usedSlot, adjust start
                val newSlot = slot.copy(start = usedSlot.end)
                if Duration.between(newSlot.start.toTemporal, newSlot.end.toTemporal).compareTo(duration.toDuration) >= 0 then List(newSlot)
                else Nil
              else if slot.start.isBefore(usedSlot.start) && slot.end.isAfter(usedSlot.start) then
                // Slot overlaps start of usedSlot, adjust end
                val newSlot = slot.copy(end = usedSlot.start)
                if Duration.between(newSlot.start.toTemporal, newSlot.end.toTemporal).compareTo(duration.toDuration) >= 0 then List(newSlot)
                else Nil
              else Nil
            else List(slot)
          }
        }
      }


    // Schedule each viva
    val (scheduledVivas, _: List[Availability]) = preVivaList.foldLeft((List.empty[PosViva], List.empty[Availability])) { case ((acc, usedSlots), viva) =>
      val roleSet = viva.roleLinkedWithResourceList.map(_.role).toSet
      val availabilities = availabilityMap(roleSet)
      val possibleSlots = Availability.findAllPossibleAvailabilitiesSlot(availabilities, agenda.duration)

      val updatedPossibleSlots = updateAvailabilitySlots(possibleSlots, agenda.duration, usedSlots)

      val (chosenSlotOpt, updatedUsedSlots) = chooseFirstPossibleAvailabilitiesSlot(updatedPossibleSlots, agenda.duration, usedSlots)

      chosenSlotOpt match
        case Some((start, end, preference)) =>
          val scheduledViva = PosViva(
            viva.student.toString,
            viva.title.toString,
            start.toString,
            end.toString,
            preference,
            (viva.roleLinkedWithResourceList.collectFirst { case RoleLinkedWithResource(p: President, name, _) => name }getOrElse()).toString,
            (viva.roleLinkedWithResourceList.collectFirst { case RoleLinkedWithResource(p: Advisor, name, _) => name }getOrElse()).toString,
            viva.roleLinkedWithResourceList.collect { case RoleLinkedWithResource(s: Supervisor, name, _) => name.toString },
             viva.roleLinkedWithResourceList.collect { case RoleLinkedWithResource(c: CoAdvisor, name, _) => name.toString },
          )
          (scheduledViva :: acc, updatedUsedSlots)
        case None => (acc, usedSlots) // If no slot found, ignore the viva
    }
    scheduledVivas.foreach(println)
    Right(())



