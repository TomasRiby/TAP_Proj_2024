package pj.domain.schedule

import scala.language.adhocExtensions
import org.scalatest.funsuite.AnyFunSuite
import pj.domain.Teacher
import pj.io.FileIO
import pj.xml.XML

class ScheduleMS01Test extends AnyFunSuite:

  test("Accessing all teachers more readably"):
    val filePath = "files/assessment/ms01/valid_agenda_01_in.xml"
    val teachersIdsResult = for {
      xml <- FileIO.load(filePath)
      resources <- XML.fromNode(xml, "resources")
      teachers <- XML.fromNode(resources, "teachers")
    } yield extractTeachersIds(teachers)

    // Handle the result
    teachersIdsResult match
      case Right(ids) => ids.foreach(id => println(s"Teacher ID: $id"))
      case Left(error) => println(s"Error: $error")

  // Helper method to extract teacher IDs from the teachers node
  def extractTeachersIds(teachers: scala.xml.Node): List[String] =
    (teachers \ "teacher").flatMap { teacherNode =>
      XML.fromAttribute(teacherNode, "id").toOption
    }.toList
