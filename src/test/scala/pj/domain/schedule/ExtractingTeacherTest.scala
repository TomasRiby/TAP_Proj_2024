package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.{Agenda, Availability, Teacher}
import pj.io.FileIO
import pj.xml.XML

import scala.language.adhocExtensions
import scala.xml.Node

class ExtractingTeacherTest extends AnyFunSuite:

  test("Accessing all teachers more readably"):
    val filePath = "files/assessment/ms01/valid_agenda_01_in.xml"
    val teachersIdsResult = for {
      xml <- FileIO.load(filePath)
      resources <- XML.fromNode(xml, "resources")
      teachers <- XML.fromNode(resources, "teachers")
    } yield extractTeachers(teachers)
    teachersIdsResult match
      case Right(ids) => ids.foreach(println(_))
      case Left(error) => println(s"Error: $error")

  // Helper method to extract teacher IDs from the teachers node
  private def extractTeachers(teachersNode: Node): Seq[Teacher] =
    (teachersNode \ "teacher").flatMap { node =>
      for {
        id <- XML.fromAttribute(node, "id").toOption
        name <- XML.fromAttribute(node, "name").toOption
        availability <- Some(extractAvailability(node))
      } yield Teacher(id, name, availability)
    }

  private def extractAvailability(availabilityNode: Node): Seq[Availability] =
    (availabilityNode \ "availability").flatMap { node =>
      for {
        start <- XML.fromAttribute(node, "start").toOption
        end <- XML.fromAttribute(node, "end").toOption
        preference <- XML.fromAttribute(node, "preference").toOption
      } yield Availability(start, end, preference)
    }


  test("Test to see if it does the same thing"):
    val ListTeachers = Agenda.loadTeachers("files/assessment/ms01/valid_agenda_05_in.xml")
    println(ListTeachers)

//    val trueValue = "List(Teacher(T001,Teacher 001,List(Availability(2024-05-30T13:30:00,2024-05-30T17:30:00,3), Availability(2024-05-30T09:00:00,2024-05-30T12:30:00,5))), Teacher(T002,Teacher 002,List(Availability(2024-05-30T14:30:00,2024-05-30T17:40:00,5), Availability(2024-05-30T10:30:00,2024-05-30T12:00:00,5))), Teacher(T003,Teacher 003,List(Availability(2024-05-30T08:30:00,2024-05-30T12:00:00,5))))"
//
//    equals(ListTeachers, trueValue)