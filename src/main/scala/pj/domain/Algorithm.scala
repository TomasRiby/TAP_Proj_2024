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

    algorithmBST2(preVivaList, duration)
  //    algorithmFCFS(preVivaList, duration)


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

  def algorithmBST2(preVivaList: Seq[PreViva], duration: ODuration): Result[ScheduleOut] =
    val initialPossibleSlots = preVivaList.map { preViva =>
      val preVivaVal = PreViva.hashSetOfIds(preViva)
      val allAvailabilities = preViva.roleLinkedWithResourceList.map(_.listAvailability)
      val updatedAvailabilityList = Availability.findAllPossibleAvailabilitiesSlot(allAvailabilities, duration)
      (preViva, updatedAvailabilityList)
    }
    val allCombinations = generateAllCombinations(initialPossibleSlots, List.empty, List.empty, duration)

    val bestSchedule = allCombinations.foldLeft(List.empty[(PreViva, Availability)]) { (best, current) =>
      val bestSum = best.map(_._2.preference.toInteger).sum
      val currentSum = current.map(_._2.preference.toInteger).sum
      if currentSum > bestSum then current else best
    }

    // Convert the best schedule to ScheduleOut
    val scheduledVivas = bestSchedule.map { case (preViva, availability) =>
      PosViva.chosenAvailabilityToPosViva(availability.start.toLocalDateTime, availability.end.toLocalDateTime, availability.preference.toInteger, preViva)
    }.sortBy(v => (LocalDateTime.parse(v.start, formatter), v.preference))

    Right(ScheduleOut.from(scheduledVivas))

  def generateAllCombinations(possibleSlots: Seq[(PreViva, List[Availability])], currentSchedule: List[(PreViva, Availability)], usedSlots: List[(HashSet[ID], Availability)], duration: ODuration): List[List[(PreViva, Availability)]] =
    possibleSlots.headOption match
      case Some((currentPreViva, availabilities)) =>
        availabilities.flatMap { availability =>
          if isCompatible(currentSchedule, currentPreViva, availability) then
            val newIds = PreViva.hashSetOfIds(currentPreViva)
            val updatedUsedSlots = (newIds, availability) :: usedSlots
            val updatedAvailabilityList = Availability.updateVivasBasedOnUsedSlots(currentPreViva, updatedUsedSlots, newIds, duration)
            val nextPossibleSlots = possibleSlots.drop(1).map { case (preViva, _) =>
              val updatedAvailabilities = Availability.updateVivasBasedOnUsedSlots(preViva, updatedUsedSlots, newIds, duration)
              (preViva, Availability.findAllPossibleAvailabilitiesSlot(updatedAvailabilities, duration))
            }
            generateAllPossibleIntervals(availability, duration).flatMap { interval =>
              generateAllCombinations(nextPossibleSlots, (currentPreViva, interval) :: currentSchedule, updatedUsedSlots, duration)
            }
          else
            List.empty
        }
      case None =>
        List(currentSchedule)

  def generateAllPossibleIntervals(availability: Availability, duration: ODuration): List[Availability] =
    val start = availability.start.toLocalDateTime
    val end = availability.end.toLocalDateTime
    val intervalDuration = duration.toDuration

    @tailrec
    def loop(currentStart: LocalDateTime, acc: List[Availability]): List[Availability] =
      if currentStart.plus(intervalDuration).isAfter(end) then acc
      else
        val currentEnd = currentStart.plus(intervalDuration)
        val newAvailability = Availability.from(OTime.from(currentStart), OTime.from(currentEnd), availability.preference)
        loop(currentStart.plusMinutes(30), newAvailability :: acc)
    loop(start, Nil).reverse

  def isCompatible(schedule: List[(PreViva, Availability)], newPreViva: PreViva, newAvailability: Availability): Boolean =
    !schedule.exists { case (preViva, availability) =>
      Availability.intersects(availability, newAvailability) && PreViva.hashSetOfIds(preViva).intersect(PreViva.hashSetOfIds(newPreViva)).nonEmpty
    }

