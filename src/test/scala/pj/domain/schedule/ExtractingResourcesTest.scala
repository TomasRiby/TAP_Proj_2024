package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.{Agenda, Availability}
import pj.io.{FileIO, ResourceIO}
import pj.xml.XML

import scala.language.adhocExtensions
import scala.xml.Node

class ExtractingResourcesTest extends AnyFunSuite:

//  test("Accessing all teachers more readably"):
//    val filePath = "files/assessment/ms01/valid_agenda_01_in.xml"
//    val teachersIdsResult = for {
//      xml <- FileIO.load(filePath)
//      resources <- XML.fromNode(xml, "resources")
//      teachers <- XML.fromNode(resources, "teachers")
//    } yield extractTeachers(teachers)
//    teachersIdsResult match
//      case Right(ids) => ids.foreach(println(_))
//      case Left(error) => println(s"Error: $error")
//
//  // Helper method to extract teacher IDs from the teachers node
//  private def extractTeachers(teachersNode: Node): Seq[Teacher] =
//    (teachersNode \ "teacher").flatMap { node =>
//      for {
//        id <- XML.fromAttribute(node, "id").toOption
//        name <- XML.fromAttribute(node, "name").toOption
//        availability <- Some(extractAvailability(node))
//      } yield Teacher(id, name, availability)
//    }
//
//  private def extractAvailability(availabilityNode: Node): Seq[Availability] =
//    (availabilityNode \ "availability").flatMap { node =>
//      for {
//        start <- XML.fromAttribute(node, "start").toOption
//        end <- XML.fromAttribute(node, "end").toOption
//        preference <- XML.fromAttribute(node, "preference").toOption
//      } yield Availability(start, end, preference)
//    }


  test("Test to see if it does the same thing"):
    val ListTeachers = ResourceIO.loadTeachers("files/assessment/ms01/valid_agenda_01_in.xml")
    println(ListTeachers)
