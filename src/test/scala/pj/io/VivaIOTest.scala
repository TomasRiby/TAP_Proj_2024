package pj.io

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import pj.domain.{Result, Viva}

import scala.xml.Elem

class VivaIOTest extends AnyFunSuite with Matchers:
  val simpleXml = "files/test/ms01/simple01.xml"

  test("Load values from xml and instance proper Viva"):
    val resultElem: Result[Elem] = FileIO.load(simpleXml);
    val result = resultElem.fold(
      error => Left(error),
      elem => VivaIO.loadViva(elem)
    )

    result match
      case Right(viva) =>
        viva.foreach(v =>
          assert(v.student === "Student 001")
          assert(v.title === "Title 1")
          assert(v.president.id === "T001")
          assert(v.supervisor.id === "E001")
          assert(v.advisor.id === "T002")
        )
      case Left(_) => fail("Did not create VIVA")
