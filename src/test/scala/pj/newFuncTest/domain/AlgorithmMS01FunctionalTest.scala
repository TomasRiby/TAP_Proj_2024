package pj.newFuncTest.domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.OptionValues
import org.scalatest.EitherValues
import pj.domain.Algorithm
import pj.domain.schedule.ScheduleMS03
import pj.io.{AgendaIO, FileIO}

import scala.xml.XML

class AlgorithmMS01FunctionalTest extends AnyFlatSpec with Matchers with OptionValues with EitherValues {

  "AlgorithmMS01" should "generate the correct schedule from the input XML" in:
    val dir = "src/test/scala/pj/newFuncTest/resources/"
    val fileName = "ms01-func1.xml"
    val filePath = dir + fileName

    val result = for {
      fileLoaded <- FileIO.load(filePath)
      agenda <- AgendaIO.loadAgenda(fileLoaded)
      scheduleOut <- Algorithm.MS01_Algorithm(agenda)
    } yield scheduleOut
    result shouldBe a[Right[?, ?]]

    result.map { scheduleOut =>
      // Validação do totalPreference
      scheduleOut.preference should be(25)

      // Validação do número de posVivas
      scheduleOut.posVivas should have length 2

      // Validação dos campos da primeira viva
      val viva1 = scheduleOut.posVivas.find(_.student == "Student 001").value
      viva1.title should be("Title 1")
      viva1.start should be("2024-05-30T10:30:00")
      viva1.end should be("2024-05-30T11:30:00")
      viva1.preference should be(12)
      viva1.president should be("Teacher 001")
      viva1.advisor should be("Teacher 002")
      viva1.supervisors should contain("External 001")

      // Validação dos campos da segunda viva
      val viva2 = scheduleOut.posVivas.find(_.student == "Student 002").value
      viva2.title should be("Title 2")
      viva2.start should be("2024-05-30T15:30:00")
      viva2.end should be("2024-05-30T16:30:00")
      viva2.preference should be(13)
      viva2.president should be("Teacher 002")
      viva2.advisor should be("Teacher 001")
      viva2.supervisors should contain("External 001")
    }


}