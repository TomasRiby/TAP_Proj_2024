package test

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.schedule.ScheduleMS01
import pj.io.FileIO

class Test extends AnyFunSuite:
  test("aaaaaaa"):
    val dir = "files/assessment/ms01/"
    val fileName = "valid_agenda_01_in.xml"
    val filePath = dir + fileName
    val result = for {
      fileLoaded <- FileIO.load(filePath)
      result <- ScheduleMS01.create(fileLoaded)
    } yield result

