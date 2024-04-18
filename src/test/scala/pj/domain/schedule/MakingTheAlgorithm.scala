package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.{Agenda, Availability, DomainError, Resource, Result, Viva}
import pj.io.{AgendaIO, FileIO, ResourceIO}
import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, ODuration, Preference}
import pj.xml.XML

import java.io.File
import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec
import scala.language.adhocExtensions
import scala.xml.Node

class MakingTheAlgorithm extends AnyFunSuite:

  test("Test a single test file from the assessment directory"):
    val dir = "files/assessment/ms01/"
    val fileName = "valid_agenda_03_in.xml"
    val filePath = dir + fileName
    val result = for {
      fileLoaded <- FileIO.load(filePath)
      result <- AgendaIO.loadAgenda(fileLoaded)
    } yield result


    case class RoleAvailabilities(id: Any, availabilities: List[(ID, List[Availability])])

    case class ScheduleViva(president: RoleAvailabilities, advisor: RoleAvailabilities, supervisor: RoleAvailabilities)


    result match
      case Right(agenda) => extractAvailabilites(agenda)
      case Left(error) => println(s"Erro ao carregar a agenda: $error")


    def extractAvailabilites(agenda: Agenda): Any =

      // Given a structure representing teachers' availabilities
      val teacherAvailabilities = agenda.resources.teacher.flatMap { teacher =>
        teacher.availability.map { avail =>
          (teacher.id, avail)
        }
      }
      val externalAvailabilities = agenda.resources.external.flatMap { external =>
        external.availability.map { avail =>
          (external.id, avail)
        }
      }

      val groupedTeacherList = teacherAvailabilities
        .groupBy(_._1)
        .map { case (teacherId, availList) =>
          (teacherId, availList.map(_._2))
        }
        .toList

      val groupedExternalList = externalAvailabilities
        .groupBy(_._1)
        .map { case (externalId, availList) =>
          (externalId, availList.map(_._2))
        }
        .toList

      val groupedAvailabilitiesList = groupedTeacherList ++ groupedExternalList


      val vivas = agenda.vivas
      val duration = agenda.duration

      def CreateSchedule(vivas: Seq[Viva], teacherAvai: List[(ID, List[Availability])]): Seq[ScheduleViva] =
        vivas.map { viva =>
          ScheduleViva(
            president = RoleAvailabilities(viva.president, teacherAvai.filter(_._1 == viva.president.id)),
            advisor = RoleAvailabilities(viva.advisor, teacherAvai.filter(_._1 == viva.advisor.id)),
            supervisor = RoleAvailabilities(viva.supervisor, teacherAvai.filter(_._1 == viva.supervisor.id))
          )
        }

      val scheduleVivaList = CreateSchedule(vivas, groupedAvailabilitiesList)

      def ExtractAvail(scheduleViva: ScheduleViva): Result[List[Availability]]=
        val presidentAval = scheduleViva.president.availabilities.flatMap(_._2)
        val advisorAval = scheduleViva.advisor.availabilities.flatMap(_._2)
        val supervisorAval = scheduleViva.supervisor.availabilities.flatMap(_._2)
        val results = findBestCombinedAvailability(presidentAval, advisorAval, supervisorAval, duration)
        results


      def findBestCombinedAvailability(presAvails: List[Availability], advAvails: List[Availability], supAvails: List[Availability], requiredDuration: ODuration): Result[List[Availability]] =
        def overlapThree(a1: List[Availability], a2: List[Availability], a3: List[Availability], requiredDuration: ODuration): List[Availability] =
          for {
            avail1 <- a1
            avail2 <- a2
            avail3 <- a3
            start = List(avail1.start, avail2.start, avail3.start).foldLeft(avail1.start)((acc, x) => if (x.isAfter(acc)) x else acc)
            end = List(avail1.end, avail2.end, avail3.end).foldLeft(avail1.end)((acc, x) => if (x.isBefore(acc)) x else acc)
            if start.isBefore(end) && Duration.between(start.toTemporal, end.toTemporal).compareTo(requiredDuration.toDuration) >= 0
          } yield Availability(start, end, Preference.add(avail1.preference, avail2.preference, avail3.preference))

        val totalOverlap = overlapThree(presAvails, advAvails, supAvails, requiredDuration)
        if (totalOverlap.isEmpty)
          Left(DomainError.Error("No valid overlapping availabilities found"))
        else
          Right(totalOverlap)

      def durationMatches(required: Duration, start: LocalDateTime, end: LocalDateTime): Boolean =
        Duration.between(start, end) == required

      def noOverlaps(existing: List[Availability], candidate: Availability): Boolean =
        !existing.exists(e => (e.start.isBefore(candidate.end) && e.end.isAfter(candidate.start)) ||
          (candidate.start.isBefore(e.end) && candidate.end.isAfter(e.start)))

      def selectMatchingAvailabilities(existing: List[Availability], candidates: List[Availability], requiredDuration: Duration): List[Availability] =
        candidates.filter(candidate => noOverlaps(existing, candidate) && durationMatches(requiredDuration, candidate.start.toLocalDateT, candidate.end.toLocalDateT))

      def processSchedules(schedules: List[Result[List[Availability]]], requiredDuration: Duration): List[Availability] =
        @tailrec
        def helper(schedules: List[Result[List[Availability]]], booked: List[Availability], acc: List[Availability]): List[Availability] =
          println("--------------------------------")
          println(booked)
          println("--------------------------------")

          schedules match
            case Nil => acc
            case Right(avails) :: tail =>
              val validAvails = selectMatchingAvailabilities(booked, avails, requiredDuration)
              helper(tail, booked ++ validAvails, acc ++ validAvails)
            case Left(_) :: tail => helper(tail, booked, acc)

        helper(schedules, List.empty, List.empty)


      val result = scheduleVivaList.foldLeft(List.empty[Result[List[Availability]]]) { (acc, viva) =>
        acc ++ List(ExtractAvail(viva))
      }

      val finalSelections = processSchedules(result,duration.toDuration)

      println("------------------------------------------------------------------")
//      println(result)
      println("------------------------------------------------------------------")
//      println(finalSelections)


