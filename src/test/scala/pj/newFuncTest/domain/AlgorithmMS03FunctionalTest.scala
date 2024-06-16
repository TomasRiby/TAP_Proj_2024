package pj.newFuncTest.domain

import org.scalatest.{EitherValues, OptionValues}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.domain.{Algorithm, PosViva}
import pj.domain.schedule.ScheduleMS03
import pj.io.{AgendaIO, FileIO}

import scala.xml.XML

class AlgorithmMS03FunctionalTest extends AnyFlatSpec with Matchers with OptionValues with EitherValues {

  "AlgorithmMS01" should "generate the correct schedule from the input XML" in:
    val dir = "src/test/scala/pj/newFuncTest/resources/"
    val fileName = "ms03-func1.xml"
    val filePath = dir + fileName

    val result = for {
      fileLoaded <- FileIO.load(filePath)
      agenda <- AgendaIO.loadAgenda(fileLoaded)
      scheduleOut <- Algorithm.MS03_Algorithm(agenda)
    } yield scheduleOut
    result shouldBe a[Right[?, ?]]

    result.map { scheduleOut =>
      // Validação do totalPreference
      scheduleOut.preference should be(104)

      // Validação do número de posVivas
      scheduleOut.posVivas should have length 9

      // Validação dos campos de cada viva
      val expectedVivas = List(
        PosViva.from("Student006", "Student006", "2024-06-04T14:47:00", "2024-06-04T15:47:00", 3, "T001", "T002", List(), List("E001")),
        PosViva.from("Student001", "Student001", "2024-06-05T09:00:00", "2024-06-05T10:00:00", 16, "T002", "T001", List(), List("E001", "E002")),
        PosViva.from("Student004", "Student004", "2024-06-05T10:00:00", "2024-06-05T11:00:00", 16, "T001", "T002", List(), List("E001", "E002")),
        PosViva.from("Student007", "Student007", "2024-06-08T17:14:00", "2024-06-08T18:14:00", 16, "T002", "T001", List("E001", "E002"), List()),
        PosViva.from("Student002", "Student002", "2024-06-18T15:45:00", "2024-06-18T16:45:00", 15, "T002", "T001", List("E002"), List()),
        PosViva.from("Student005", "Student005", "2024-06-19T11:59:00", "2024-06-19T12:59:00", 10, "T001", "T002", List(), List()),
        PosViva.from("Student009", "Student009", "2024-06-20T08:34:00", "2024-06-20T09:34:00", 10, "T002", "T001", List(), List()),
        PosViva.from("Student003", "Student003", "2024-06-20T14:44:00", "2024-06-20T15:44:00", 15, "T001", "T002", List("E002"), List()),
        PosViva.from("Student008", "Student008", "2024-06-23T10:01:00", "2024-06-23T11:01:00", 3, "T001", "T002", List(), List("E002"))
      )

      expectedVivas.foreach { expectedViva =>
        val actualViva = scheduleOut.posVivas.find(_.student == expectedViva.student).value
        actualViva.title should be(expectedViva.title)
        actualViva.start should be(expectedViva.start)
        actualViva.end should be(expectedViva.end)
        actualViva.preference should be(expectedViva.preference)
        actualViva.president should be(expectedViva.president)
        actualViva.advisor should be(expectedViva.advisor)
        actualViva.supervisors should contain theSameElementsAs expectedViva.supervisors
        actualViva.coAdvisors should contain theSameElementsAs expectedViva.coAdvisors
      }
    }
}