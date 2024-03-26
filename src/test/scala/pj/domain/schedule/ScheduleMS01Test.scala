package pj.domain.schedule

import scala.language.adhocExtensions
import org.scalatest.funsuite.AnyFunSuite
import pj.io.FileIO
import pj.xml.XML

// TODO: Create the code to test a functional domain model for schedule creation.
//       create files in the files/test/ms01 folder
class ScheduleMS01Test extends AnyFunSuite:
//Isto é so random
  test("fromNode should return correct node if it exists"):
    val result = FileIO.load("files/test/ms01/simple01.xml")
    result match {
      case Right(xml) =>
        val resources = (xml \\ "resources").headOption
        resources match
          case Some(xml) =>
            val teachers = (xml \\ "teachers").headOption
            teachers match
              case Some(xml) =>
                println(xml)
              case None =>
                println("NO TEACHERS")
          case None =>
            println("No <viva> element found in the XML.")
      case Left(error) =>
        println(s"Error loading XML file: $error")
    }