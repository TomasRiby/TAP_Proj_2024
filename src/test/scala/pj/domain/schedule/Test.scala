package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.*
import pj.io.{AgendaIO, FileIO, ResourceIO}
import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, ODuration, Preference}
import pj.xml.XML

import java.io.File
import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec
import scala.language.adhocExtensions
import scala.xml.Node

class Test extends AnyFunSuite:

  test("Test a single test file from the assessment directory"):
    val dir = "files/test/ms01/"
    val fileName = "invalid_external_Id_in.xml"
    val filePath = dir + fileName
    val result = for {
      fileLoaded <- FileIO.load(filePath)
      result <- ScheduleMS01.create(fileLoaded)
    } yield result
    println(result)


