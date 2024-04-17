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
    val fileName = "valid_agenda_03_in.xml"
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
        val presidentAval = scheduleViva.president.availabilities.flatMap(_._2)
        println(presidentAval)
        val advisorAval = scheduleViva.advisor.availabilities.flatMap(_._2)
        println(advisorAval)
        val supervisorAval = scheduleViva.supervisor.availabilities.flatMap(_._2)
        println(supervisorAval)
        findBestCombinedAvailability(presidentAval, advisorAval, supervisorAval)

      def findBestCombinedAvailability(presAvails: List[Availability], advAvails: List[Availability], supAvails: List[Availability]): Option[Availability] =
        // Function to find overlapping availability between two lists
        def overlap(a1: List[Availability], a2: List[Availability]): List[Availability] =
          for {
            avail1 <- a1
            avail2 <- a2
            start = if (avail1.start.isAfter(avail2.start)) avail1.start else avail2.start
            end = if (avail1.end.isBefore(avail2.end)) avail1.end else avail2.end
            if start.isBefore(end)
          } yield Availability(start, end, Preference.add(avail1.preference, avail2.preference))

        // Find overlaps between president and advisor
        val presAdvOverlap = overlap(presAvails, advAvails)

        // Find overlaps between the result and supervisor
        val totalOverlap = overlap(presAdvOverlap, supAvails)

        // Return the overlap with the highest preference sum

        val result = totalOverlap.reduceOption((a, b) => if (Preference.maxPreference(a.preference, b.preference) == a.preference) a else b)
        println(result)
        result















