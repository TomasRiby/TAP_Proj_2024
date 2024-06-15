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

object AlgorithmMS03:
  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  def MS03_Algorithm(agenda: Agenda): Result[ScheduleOut] =
    val teacherList = agenda.resources.teacher
    val externalList = agenda.resources.external
    val duration = agenda.duration
    val expectedTitles = agenda.vivas.map(_.title).toSet // Conjunto de títulos esperados a partir do XML

    val preVivaList = agenda.vivas.flatMap { viva =>
      if expectedTitles.contains(viva.title) then
        val preViva = PreViva.linkVivaWithResource(viva, teacherList, externalList)
        println(s"Generated PreViva: ${preViva.title}")
        Some(preViva)
      else
        None
    }

    println("Starting Brute Force Algorithm with Heuristics")
    val bfResult = algorithmBF(preVivaList, duration, maxCombinations = 1000)
    bfResult.foreach { result =>
      println(s"Brute Force Result: ${result.posVivas.map(_.preference).sum}")
      result.posVivas.foreach { viva =>
        println(s"Brute Force Viva: ${viva.title}, Preference: ${viva.preference}, Start: ${viva.start}, End: ${viva.end}")
      }
    }

    println("Starting FCFS Algorithm")
    val fcfsResult = algorithmFCFS(preVivaList, duration)
    fcfsResult.foreach { result =>
      println(s"FCFS Result: Total Preference = ${result.posVivas.map(_.preference).sum}")
      result.posVivas.foreach { viva =>
        println(s"FCFS Viva: ${viva.title}, Preference: ${viva.preference}, Start: ${viva.start}, End: ${viva.end}")
      }
    }
    bfResult

  def algorithmFCFS(preVivaList: Seq[PreViva], duration: ODuration): Result[ScheduleOut] =
    val schedulingResult = schedulePreVivas(preVivaList, duration)
    schedulingResult.map { case (scheduledVivas, _) =>
      val sortedScheduledVivas = scheduledVivas.sortBy(v => (LocalDateTime.parse(v.start, formatter), v.preference))
      ScheduleOut.from(sortedScheduledVivas)
    }

  def algorithmBF(preVivaList: Seq[PreViva], duration: ODuration, maxCombinations: Int): Result[ScheduleOut] =
    val allCombinations = preVivaList.permutations.take(maxCombinations).toList
    val validSchedules = allCombinations.flatMap { combination =>
      val result = schedulePreVivas(combination, duration)
      result match
        case Right((scheduledVivas, _)) => Some(scheduledVivas)
        case Left(_) => None
    }

    validSchedules.headOption match
      case None => Left(DomainError.ImpossibleSchedule)
      case Some(initialBestSchedule) =>
        val bestSchedule = validSchedules.foldLeft(initialBestSchedule) { (best, current) =>
          if current.map(_.preference).sum > best.map(_.preference).sum then current else best
        }
        val totalPreference = bestSchedule.map(_.preference).sum
        println(s"Best Schedule from Brute Force: Total Preference = $totalPreference")
        bestSchedule.foreach { viva =>
          println(s"Best Schedule Viva: ${viva.title}, Preference: ${viva.preference}, Start: ${viva.start}, End: ${viva.end}")
        }
        Right(ScheduleOut.from(bestSchedule))

  private def schedulePreVivas(preVivaList: Seq[PreViva], duration: ODuration): Result[(List[PosViva], List[(HashSet[ID], Availability)])] =
    preVivaList.foldLeft[Result[(List[PosViva], List[(HashSet[ID], Availability)])]](Right((List.empty[PosViva], List.empty[(HashSet[ID], Availability)]))):
      case (postViva, preViva) =>
        postViva.flatMap { case (posVivaList, usedSlots) =>
          val newIds = PreViva.hashSetOfIds(preViva)
          val updatedAvailabilityList = Availability.updateVivasBasedOnUsedSlots(preViva, usedSlots, newIds, duration)
          val allPossibleSlots = Availability.findAllPossibleAvailabilitiesSlot(updatedAvailabilityList, duration)
          val firstChosenAvailability = Availability.chooseFirstPossibleAvailability(allPossibleSlots, duration, usedSlots, newIds)
          firstChosenAvailability._1 match
            case Some((start, end, preference)) =>
              println(s"Scheduling Viva: ${preViva.title} from ${start.format(formatter)} to ${end.format(formatter)} with preference $preference")
              val chosenPosViva = PosViva.chosenAvailabilityToPosViva(start, end, preference, preViva)
              Right((chosenPosViva :: posVivaList, firstChosenAvailability._2))
            case None =>
              Left(DomainError.ImpossibleSchedule)
        }
