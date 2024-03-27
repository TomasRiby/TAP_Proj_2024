package pj.domain.schedule

import scala.language.adhocExtensions
import org.scalatest.funsuite.AnyFunSuite
import pj.io.FileIO
import pj.xml.XML

// TODO: Create the code to test a functional domain model for schedule creation.
//       create files in the files/test/ms01 folder
class ScheduleMS01Test extends AnyFunSuite:
  //Isto é so random
  test("Just testing things"):
    //loads the file
    val result = FileIO.load("files/test/ms01/simple01.xml")
    result match {
      //if successful
      case Right(xml) =>
        //load resources
        val resources = XML.fromNode(xml, "resources")
        print(resources)
      case Left(error) =>
        println(s"Error loading XML file: $error")
    }