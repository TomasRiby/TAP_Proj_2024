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
        def overlapThree(a1: List[Availability], a2: List[Availability], a3: List[Availability]): List[Availability] =
          for {
            avail1 <- a1
            avail2 <- a2
            avail3 <- a3
            start = List(avail1.start, avail2.start, avail3.start).foldLeft(avail1.start)((acc, x) => if (x.isAfter(acc)) x else acc)
            end = List(avail1.end, avail2.end, avail3.end).foldLeft(avail1.end)((acc, x) => if (x.isBefore(acc)) x else acc)
            if start.isBefore(end)
          } yield Availability(start, end, Preference.add(Preference.add(avail1.preference, avail2.preference), avail3.preference))

        // Find overlaps between the result and supervisor
        val totalOverlap = overlapThree(presAvails, advAvails, supAvails)

        // Return the overlap with the highest preference sum

        val result = totalOverlap.foldLeft(None: Option[Availability]) { (acc, avail) =>
          acc match
            case Some(a) if Preference.maxPreference(a.preference, avail.preference) == a.preference => acc
            case _ => Some(avail)
        }
        println("-----------------------------------------------------")
        println(result)
        println("-----------------------------------------------------")
        result















