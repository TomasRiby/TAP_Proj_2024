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
    @tailrec
    def scheduleVivas(remainingVivas: Seq[PreViva], scheduledVivas: List[PosViva], usedSlots: List[(HashSet[ID], Availability)]): Result[List[PosViva]] =
      if remainingVivas.isEmpty then Right(scheduledVivas)
      else
        val possibleSchedules = generatePossibleSchedules(remainingVivas.toList, usedSlots, duration)
        chooseBestSchedule(possibleSchedules) match
          case None => Left(DomainError.ImpossibleSchedule)
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

    val schedulingResult = scheduleVivas(preVivaList, List.empty[PosViva], List.empty[(HashSet[ID], Availability)])
    schedulingResult.map { scheduledVivas =>
      val sortedScheduledVivas = scheduledVivas.sortBy(v => (LocalDateTime.parse(v.start, formatter), v.preference))
      ScheduleOut.from(sortedScheduledVivas)
    }

  private def generatePossibleSchedules(remainingVivas: List[PreViva], usedSlots: List[(HashSet[ID], Availability)], duration: ODuration): List[(PreViva, (Option[(LocalDateTime, LocalDateTime, Int)], List[(HashSet[ID], Availability)]))] =
    remainingVivas.flatMap { preViva =>
      val newIds = PreViva.hashSetOfIds(preViva)
      val updatedAvailabilityList = Availability.updateVivasBasedOnUsedSlots(preViva, usedSlots, newIds, duration)
      val allPossibleSlots = Availability.findAllPossibleAvailabilitiesSlot(updatedAvailabilityList, duration)
      val bestChosenAvailability = chooseBestPossibleAvailability(allPossibleSlots, duration, usedSlots, newIds)
      bestChosenAvailability._1.map(avail => (preViva, (Some(avail), bestChosenAvailability._2)))
    }

  private def chooseBestSchedule(possibleSchedules: List[(PreViva, (Option[(LocalDateTime, LocalDateTime, Int)], List[(HashSet[ID], Availability)]))]): Option[(PreViva, (Option[(LocalDateTime, LocalDateTime, Int)], List[(HashSet[ID], Availability)]))] =
    possibleSchedules.headOption.map { initial =>
      possibleSchedules.foldLeft(initial):
        case (best, current) =>
          (best._2._1, current._2._1) match
            case (Some((_, _, bestPreference)), Some((_, _, currentPreference))) if currentPreference > bestPreference =>
              current
            case _ => best
    }

  def chooseBestPossibleAvailability(availabilities: List[Availability], duration: ODuration, usedSlots: List[(HashSet[ID], Availability)], newIds: HashSet[ID]): (Option[(LocalDateTime, LocalDateTime, Int)], List[(HashSet[ID], Availability)]) =
    availabilities.foldLeft[(Option[(LocalDateTime, LocalDateTime, Int)], List[(HashSet[ID], Availability)])]((None, usedSlots)):
      case ((bestOption, bestUsedSlots), slot) =>
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

