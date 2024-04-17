package pj.io

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

import scala.util.{Failure, Success}
import scala.xml.XML
import pj.domain.{Agenda, Result}

class AgendaIOTest extends AnyFunSuite:

  val invalid_Availability_in = "files/test/ms01/invalid_Availability_in.xml"
  val invalid_external_Id_in = "files/test/ms01/invalid_Availability_in.xml"

  test("loadAgenda returns Left for invalid_Availability_in"):
    val result = AgendaIO.loadAgenda(invalid_Availability_in)

    result match
      case Right(agenda) => fail("The availability format is wrong.")
      case Left(error) => succeed

  test("loadAgenda returns Left for invalid_external_Id_in"):
      val result = AgendaIO.loadAgenda(invalid_Availability_in)

      result match
        case Right(agenda) => fail("The externalID format is wrong.")
        case Left(error) => succeed