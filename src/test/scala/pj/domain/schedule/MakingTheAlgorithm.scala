package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.{Agenda, Availability, DomainError, Resource, Result, Viva}
import pj.io.{AgendaIO, FileIO, ResourceIO}
import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, Preference}
import pj.xml.XML

import java.io.File
import java.time.LocalDateTime
import scala.language.adhocExtensions
import scala.xml.Node

class MakingTheAlgorithm extends AnyFunSuite:

  test("Test a single test file from the assessment directory"):
    val dir = "files/assessment/ms01/"
    val fileName = "valid_agenda_01_in.xml"
    val filePath = dir + fileName
    val result = AgendaIO.loadAgenda(filePath)

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

      // Transform the flat list of tuples into a list where each element is a tuple (teacherId, List[Availability])
      val groupedTeacherList = teacherAvailabilities
        .groupBy(_._1) // Group by teacherId
        .map { case (teacherId, availList) =>
          (teacherId, availList.map(_._2)) // Map to (teacherId, List of Availabilities)
        }
        .toList

      val groupedExternalList = externalAvailabilities
        .groupBy(_._1) // Group by teacherId
        .map { case (externalId, availList) =>
          (externalId, availList.map(_._2)) // Map to (teacherId, List of Availabilities)
        }
        .toList

      val groupedAvailabilitiesList = groupedTeacherList ++ groupedExternalList


      // Example print to verify the structure
      val vivas = agenda.vivas

      def CreateSchedule(vivas: Seq[Viva], teacherAvai: List[(ID, List[Availability])]): Seq[ScheduleViva] =
        vivas.map { viva =>
          ScheduleViva(
            president = RoleAvailabilities(viva.president, teacherAvai.filter(_._1 == viva.president.id)),
            advisor = RoleAvailabilities(viva.advisor, teacherAvai.filter(_._1 == viva.advisor.id)),
            supervisor = RoleAvailabilities(viva.supervisor, teacherAvai.filter(_._1 == viva.supervisor.id))
          )
        }

      val scheduleVivaList = CreateSchedule(vivas, groupedAvailabilitiesList)


      scheduleVivaList.foreach(ExtractAvail)


      def ExtractAvail(scheduleViva: ScheduleViva): Any =
        val list = scheduleViva.president.availabilities.flatMap(_._2) ++
          scheduleViva.advisor.availabilities.flatMap(_._2) ++
          scheduleViva.supervisor.availabilities.flatMap(_._2)
        findCommonAvailability(list)

      //      def findConsensusAvailability(availabilities: List[Availability]): Option[Availability] =
      ////        if (availabilities.isEmpty) return None
      //
      //        // Step 1: Sort by start time
      //        val sortedAvailabilities = availabilities.sortBy(_.start)
      //
      //        // Step 2: Find overlapping intervals with minimum preference
      //        sortedAvailabilities.reduceOption { (acc, next) =>
      //          if (acc.end.isAfter(next.start)) // There is an overlap
      //            val avail = Availability(
      //              start = if (acc.start.isAfter(next.start)) acc.start else next.start, // Maximum of start times
      //              end = if (acc.end.isBefore(next.end)) acc.end else next.end, // Minimum of end times
      //              preference = Preference.minPreference(acc.preference, next.preference) // Minimum preference
      //            )
      //            println(avail)
      //            avail
      //          else
      //            acc // No overlap, return the previous accumulation
      //        }
      def findCommonAvailability(availabilities: List[Availability]): Option[Availability] =
        if (availabilities.isEmpty) None
        else
          // Step 1: Sort by start time
          val sortedAvailabilities = availabilities.sortBy(_.start)

          // Step 2: Use foldLeft to find overlapping intervals with the minimum preference
          sortedAvailabilities.drop(1).foldLeft(sortedAvailabilities.headOption) { (currentOverlap, next) =>
            currentOverlap.flatMap { acc =>
              if (acc.end.isAfter(next.start)) // There is an overlap
                val newStart = if (acc.start.isAfter(next.start)) acc.start else next.start
                val newEnd = if (acc.end.isBefore(next.end)) acc.end else next.end

                if (newStart.isBefore(newEnd)) // Ensuring the new interval is valid
                  val minPref = Preference.minPreference(acc.preference, next.preference)
                  val result = Some(Availability(newStart, newEnd, minPref)) // Return new overlapping availability
                  println(result)
                  result
                else None // End time before start time, no valid interval
              else None // No overlap with the current availability
            }
          }











