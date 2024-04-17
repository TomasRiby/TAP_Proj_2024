package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.{Agenda, Availability, Resource, Viva}
import pj.io.{AgendaIO, FileIO, ResourceIO}
import pj.typeUtils.opaqueTypes.opaqueTypes.ID
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

      val groupedAvailabilitiesList = groupedTeacherList++groupedExternalList


      // Example print to verify the structure
      val vivas = agenda.vivas
      createSchedule(vivas, groupedAvailabilitiesList).foreach(println)

      def createSchedule(vivas: Seq[Viva], teacherAvai: List[(ID, List[Availability])]): Seq[Any] =
        vivas.map { viva =>
          ScheduleViva(
            president = RoleAvailabilities(viva.president, teacherAvai.filter(_._1 == viva.president.id)),
            advisor = RoleAvailabilities(viva.advisor, teacherAvai.filter(_._1 == viva.advisor.id)),
            supervisor = RoleAvailabilities(viva.supervisor, teacherAvai.filter(_._1 == viva.supervisor.id))
          )
        }

      case class RoleAvailabilities(id: Any, availabilities: List[(ID, List[Availability])])

      case class ScheduleViva(president: RoleAvailabilities, advisor: RoleAvailabilities, supervisor: RoleAvailabilities)








