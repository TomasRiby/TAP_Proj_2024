package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.{Agenda, Availability, DomainError, Result, Teacher}
import pj.io.{FileIO, ResourceIO}
import pj.xml.XML

import scala.language.adhocExtensions
import scala.xml.Node

class RandomTest extends AnyFunSuite:
  test("Test"):

    def loadTeachers(xmlData: String): Result[Seq[Teacher]] =
      for {
        xml <- FileIO.load(xmlData)
        //        resources <- XML.fromNode(xml, "resources")
        //        teachers <- XML.fromNode(resources, "teachers")
        extractedTeachers <- XML.traverse(xml \\ "teacher", extractTeachers)
      } yield extractedTeachers


    //    def extractTeachers[A](teacher: (String, String, Seq[Availability]) => (A))(xml: Node): Result[A] =
    //      for {
    //        id <- XML.fromAttribute(xml, "id")
    //        name <- XML.fromAttribute(xml, "name")
    //        availability <- Some(extractAvailability(xml))
    //      } yield teacher(id, name, availability)

    def extractTeachers(teacherNode: Node): Result[Teacher] =
      for
        id <- XML.fromAttribute(teacherNode, "id")
        name <- XML.fromAttribute(teacherNode, "name")
        availability <- XML.traverse(teacherNode \\ "availability", extractAvailabilities)
      yield Teacher.from(id, name, availability)


    def extractAvailabilities(availabilityNode: Node): Result[Availability] =
      for
        start <- XML.fromAttribute(availabilityNode, "start")
        end <- XML.fromAttribute(availabilityNode, "end")
        preference <- XML.fromAttribute(availabilityNode, "preference")
      yield Availability.from(start, end, preference)

    println(loadTeachers("files/assessment/ms01/valid_agenda_01_in.xml"))
