package pj.domain


import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.{OTime, Preference}
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference.Preference

import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec
import scala.collection.immutable.HashSet


object Algorithm:
  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  def MS01_Algorithm(agenda: Agenda): Result[ScheduleOut] =
    val teacherList = agenda.resources.teacher
    val externalList = agenda.resources.external
    val duration = agenda.duration

    val preVivaList = agenda.vivas.map(PreViva.linkVivaWithResource(_, teacherList, externalList))

    for {
      ms01 <- algorithmFCFS(preVivaList, duration)
      ms03 <- algorithmGreedy(preVivaList, duration)
      _ = println("FCFS: " + ms01.preference)
      _ = println("GRDY: " + ms03.preference)
    } yield ms03


  def algorithmFCFS(preVivaList: Seq[PreViva], duration: ODuration): Result[ScheduleOut] =
    val schedulingResult = preVivaList.foldLeft[Result[(List[PosViva], List[(HashSet[ID], Availability)])]](Right((List.empty[PosViva], List.empty[(HashSet[ID], Availability)]))):
      case (postViva, preViva) =>
        postViva.flatMap { case (posVivaList, usedSlots) =>
          val newIds = PreViva.hashSetOfIds(preViva)
          val updatedAvailabilityList = Availability.updateVivasBasedOnUsedSlots(preViva, usedSlots, newIds, duration)
          val allPossibleSlots = Availability.findAllPossibleAvailabilitiesSlot(updatedAvailabilityList, duration)
          val firstChosenAvailability = Availability.chooseFirstPossibleAvailability(allPossibleSlots, duration, usedSlots, newIds)
          firstChosenAvailability._1 match
            case Some((start, end, preference)) =>
              val chosenPosViva = PosViva.chosenAvailabilityToPosViva(start, end, preference, preViva)
              Right((chosenPosViva :: posVivaList, firstChosenAvailability._2))
            case None =>
              Left(DomainError.ImpossibleSchedule)
        }
    schedulingResult.map { case (scheduledVivas, _) =>
      val sortedScheduledVivas = scheduledVivas.sortBy(v => (LocalDateTime.parse(v.start, formatter), v.preference))
      ScheduleOut.from(sortedScheduledVivas)
    }

  def algorithmGreedy(preVivaList: Seq[PreViva], duration: ODuration): Result[ScheduleOut] =
    def calculateTotalPreference(scheduledVivas: List[PosViva]): Int =
      scheduledVivas.map(_.preference).sum

    @tailrec
    def scheduleVivas(remainingVivas: Seq[PreViva], scheduledVivas: List[PosViva], usedSlots: List[(HashSet[ID], Availability)]): Result[List[PosViva]] =
      if remainingVivas.isEmpty then Right(scheduledVivas)
      else
        val possibleSchedules = remainingVivas.map { preViva =>
          val newIds = PreViva.hashSetOfIds(preViva)
          val updatedAvailabilityList = Availability.updateVivasBasedOnUsedSlots(preViva, usedSlots, newIds, duration)
          val allPossibleSlots = Availability.findAllPossibleAvailabilitiesSlot(updatedAvailabilityList, duration)
          val bestChosenAvailability = chooseBestPossibleAvailability(allPossibleSlots, duration, usedSlots, newIds)
          (preViva, bestChosenAvailability)
        }.filter(_._2._1.isDefined)

        if possibleSchedules.isEmpty then Left(DomainError.ImpossibleSchedule)
        else
          val bestScheduleOption = possibleSchedules.headOption.map { initial =>
            possibleSchedules.foldLeft(initial):
              case (best, current) =>
                (best._2._1, current._2._1) match
                  case (Some((_, _, bestPreference)), Some((_, _, currentPreference))) if currentPreference > bestPreference =>
                    current
                  case _ => best
          }

          bestScheduleOption match
            case Some(bestSchedule) =>
              bestSchedule._2._1 match
                case Some((start, end, preference)) =>
                  val (bestPreViva, (_, newUsedSlots)) = bestSchedule
                  val chosenPosViva = PosViva.chosenAvailabilityToPosViva(start, end, preference, bestPreViva)
                  scheduleVivas(
                    remainingVivas.filterNot(_ == bestPreViva),
                    chosenPosViva :: scheduledVivas,
                    newUsedSlots
                  )
                case None =>
                  // This case should not happen due to the filter, but handle it to avoid MatchError
                  Left(DomainError.ImpossibleSchedule)
            case None =>
              Left(DomainError.ImpossibleSchedule)

    val schedulingResult = scheduleVivas(preVivaList, List.empty[PosViva], List.empty[(HashSet[ID], Availability)])
    schedulingResult.map { scheduledVivas =>
      val sortedScheduledVivas = scheduledVivas.sortBy(v => (LocalDateTime.parse(v.start, formatter), v.preference))
      ScheduleOut.from(sortedScheduledVivas)
    }

  def chooseBestPossibleAvailability(availabilities: List[Availability], duration: ODuration, usedSlots: List[(HashSet[ID], Availability)], newIds: HashSet[ID]): (Option[(LocalDateTime, LocalDateTime, Int)], List[(HashSet[ID], Availability)]) =
    availabilities.minByOption(-_.preference) match
      case Some(slot) =>
        val start = slot.start.toLocalDateTime
        val end = start.plus(duration.toDuration)
        val availEnd = OTime.createTime(end).getOrElse(slot.end)
        (Some((start, end, slot.preference.toInteger)), (newIds, Availability.from(slot.start, availEnd, slot.preference)) :: usedSlots)
      case None =>
        (None, usedSlots)

