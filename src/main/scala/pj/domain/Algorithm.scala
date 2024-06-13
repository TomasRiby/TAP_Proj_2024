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

    algorithmFCFS(preVivaList, duration)
    algorithmBF(preVivaList, duration)

  def algorithmFCFS(preVivaList: Seq[PreViva], duration: ODuration): Result[ScheduleOut] =
    val schedulingResult = preVivaList.foldLeft[Result[(List[PosViva], List[(HashSet[ID], Availability)])]](Right((List.empty[PosViva], List.empty[(HashSet[ID], Availability)]))):
      case (postViva, preViva) =>
        postViva.flatMap { case (posVivaList, usedSlots) =>
          val newIds = PreViva.hashSetOfIds(preViva)
          val updatedAvailabilityList = Availability.updateVivasBasedOnUsedSlots(preViva, usedSlots, newIds, duration)
          val allPossibleSlots = Availability.findAllPossibleAvailabilitiesSlot(updatedAvailabilityList, duration)
          val firstChosenAvailability = Availability.chooseFirstPossibleAvailability(allPossibleSlots, duration, usedSlots, newIds)
          firstChosenAvailability._1 match
            case Some((start: LocalDateTime, end: LocalDateTime, preference: Int)) =>
              val chosenPosViva = PosViva.chosenAvailabilityToPosViva(start, end, preference, preViva)
              Right((chosenPosViva :: posVivaList, firstChosenAvailability._2))
            case None =>
              Left(DomainError.ImpossibleSchedule)
        }
    schedulingResult.map { case (scheduledVivas, _) =>
      val sortedScheduledVivas = scheduledVivas.sortBy(v => (LocalDateTime.parse(v.start, formatter), v.preference))
      ScheduleOut.from(sortedScheduledVivas)
    }

  def algorithmBF(preVivaList: Seq[PreViva], duration: ODuration): Result[ScheduleOut] =
    val allCombinations = generateCombinations(preVivaList, duration)

    val bestSchedule = allCombinations.foldLeft[(List[PosViva], List[(HashSet[ID], Availability)])](List.empty, List.empty):
      (best, current) =>
        if (current._1.map(_.preference).sum > best._1.map(_.preference).sum) current else best

    val totalPreferences = bestSchedule._1.map(_.preference).sum
    println(s"Total Preferences: $totalPreferences")

    Right(ScheduleOut.from(bestSchedule._1))

  private def generateCombinations(preVivaList: Seq[PreViva], duration: ODuration): List[(List[PosViva], List[(HashSet[ID], Availability)])] =
    def helper(preVivas: Seq[PreViva], usedSlots: List[(HashSet[ID], Availability)]): List[(List[PosViva], List[(HashSet[ID], Availability)])] =
      preVivas.headOption match
        case None => List((List.empty, usedSlots))
        case Some(preViva) =>
          val newIds = PreViva.hashSetOfIds(preViva)
          val updatedAvailabilityList = Availability.updateVivasBasedOnUsedSlots(preViva, usedSlots, newIds, duration)
          val allPossibleSlots = Availability.findAllPossibleAvailabilitiesSlot(updatedAvailabilityList, duration)

          allPossibleSlots.flatMap { availability =>
            availability match
              case Availability(start: OTime, end: OTime, preference: Preference) =>
                val newPosViva = PosViva.chosenAvailabilityToPosViva(start.toLocalDateTime, end.toLocalDateTime, preference, preViva)
                val newUsedSlots = (newIds, Availability(start, end, preference)) :: usedSlots
                helper(preVivas.drop(1), newUsedSlots).map { case (posVivas, usedSlots) =>
                  (newPosViva :: posVivas, usedSlots)
                }
              case _ => List.empty
          }

    helper(preVivaList, List.empty)
