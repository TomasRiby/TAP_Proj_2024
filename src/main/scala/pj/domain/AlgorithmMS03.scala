package pj.domain

import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.{OTime, Preference}

import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec
import scala.collection.immutable.HashSet

object AlgorithmMS03:
  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  def algorithmGlobalGreedy(preVivaList: Seq[PreViva], duration: ODuration): Result[ScheduleOut] =
    val schedulingResult = scheduleVivas(preVivaList, List.empty[PosViva], List.empty[(HashSet[ID], Availability)], duration)
    schedulingResult.map { scheduledVivas =>
      val sortedScheduledVivas = scheduledVivas.sortBy(v => (LocalDateTime.parse(v.start, formatter), v.preference))
      ScheduleOut.from(sortedScheduledVivas)
    }

  @tailrec
  private def scheduleVivas(remainingVivas: Seq[PreViva], scheduledVivas: List[PosViva], usedSlots: List[(HashSet[ID], Availability)], duration: ODuration): Result[List[PosViva]] =
    if remainingVivas.isEmpty then
      Right(scheduledVivas)
    else
      val possibleSchedules = generateAllPossibleSchedules(remainingVivas.toList, usedSlots, duration)
      chooseBestGlobalSchedule(possibleSchedules, remainingVivas, duration) match
        case None => Left(DomainError.ImpossibleSchedule)
        case Some((bestPreViva, (Some((start, end, preference)), newUsedSlots))) =>
          val chosenPosViva = PosViva.chosenAvailabilityToPosViva(start, end, preference, bestPreViva)
          scheduleVivas(
            remainingVivas.filterNot(_ == bestPreViva),
            chosenPosViva :: scheduledVivas,
            newUsedSlots,
            duration
          )
        case Some((_, (None, _))) => Left(DomainError.ImpossibleSchedule) // Handle the case where no valid availability is found

  private def generateAllPossibleSchedules(remainingVivas: List[PreViva], usedSlots: List[(HashSet[ID], Availability)], duration: ODuration): List[(PreViva, (Option[(LocalDateTime, LocalDateTime, Int)], List[(HashSet[ID], Availability)]))] =
    remainingVivas.flatMap { preViva =>
      val newIds = PreViva.hashSetOfIds(preViva)
      val updatedAvailabilityList = Availability.updateVivasBasedOnUsedSlots(preViva, usedSlots, newIds, duration)
      val allPossibleSlots = Availability.findAllPossibleAvailabilitiesSlot(updatedAvailabilityList, duration)
      val bestChosenAvailability = chooseBestPossibleAvailability(allPossibleSlots, duration, usedSlots, newIds)
      bestChosenAvailability._1.map(avail => (preViva, (Some(avail), bestChosenAvailability._2)))
    }

  private def chooseBestGlobalSchedule(possibleSchedules: List[(PreViva, (Option[(LocalDateTime, LocalDateTime, Int)], List[(HashSet[ID], Availability)]))], remainingVivas: Seq[PreViva], duration: ODuration): Option[(PreViva, (Option[(LocalDateTime, LocalDateTime, Int)], List[(HashSet[ID], Availability)]))] =
    possibleSchedules.headOption.map { initial =>
      possibleSchedules.foldLeft(initial) { case (best, current) =>
        if evaluateSchedule(current, remainingVivas, duration) > evaluateSchedule(best, remainingVivas, duration) then
          current
        else
          best
      }
    }

  private def evaluateSchedule(schedule: (PreViva, (Option[(LocalDateTime, LocalDateTime, Int)], List[(HashSet[ID], Availability)])), remaining: Seq[PreViva], duration: ODuration): Int =
    schedule._2._1 match
      case Some((_, _, preference)) =>
        val newRemainingVivas = remaining.filterNot(_ == schedule._1)
        val futureSchedules = generateAllPossibleSchedules(newRemainingVivas.toList, schedule._2._2, duration)
        preference + futureSchedules.map(_._2._1.map(_._3).getOrElse(0)).sum
      case None => 0

  def chooseBestPossibleAvailability(availabilities: List[Availability], duration: ODuration, usedSlots: List[(HashSet[ID], Availability)], newIds: HashSet[ID]): (Option[(LocalDateTime, LocalDateTime, Int)], List[(HashSet[ID], Availability)]) =
    availabilities.foldLeft[(Option[(LocalDateTime, LocalDateTime, Int)], List[(HashSet[ID], Availability)])]((None, usedSlots)) { case ((bestOption, bestUsedSlots), slot) =>
      val start = slot.start.toLocalDateTime
      val end = start.plus(duration.toDuration)
      val availEnd = OTime.createTime(end).getOrElse(slot.end)
      val currentOption = Some((start, end, slot.preference.toInteger))
      bestOption match
        case Some((_, _, bestPreference)) if currentOption.exists(_._3 > bestPreference) =>
          (currentOption, (newIds, Availability.from(slot.start, availEnd, slot.preference)) :: usedSlots)
        case None =>
          (currentOption, (newIds, Availability.from(slot.start, availEnd, slot.preference)) :: usedSlots)
        case _ =>
          (bestOption, bestUsedSlots)
    }
  