package pj.io

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

import scala.util.{Failure, Success}
import scala.xml.{Elem, XML}
import pj.domain.{Agenda, DomainError, Result}
import pj.io.AgendaIO

class AgendaIOTest extends AnyFunSuite:

  val invalid_Availability_in = "files/test/ms01/invalid_Availability_in.xml"
  val invalid_external_Id_in = "files/test/ms01/invalid_Availability_in.xml"
  val invalid_external_Name_in = "files/test/ms01/invalid_external_Name_in.xml"
  val invalid_externalDuplicate_in = "files/test/ms01/invalid_external_Name_in.xml"
  val invalid_student_in = "files/test/ms01/invalid_external_Name_in.xml"
  val invalid_teacher_Id_in = "files/test/ms01/invalid_external_Name_in.xml"

  test("loadAgenda returns Left for invalid_Availability_in"):
    val resultElem: Result[Elem] = FileIO.load(invalid_Availability_in);
    val result = resultElem.fold(
      error => Left(error),
      elem => AgendaIO.loadAgenda(elem)
    )

    result match
      case Right(agenda) => fail("The availability format is wrong.")
      case Left(error) => succeed

  test("loadAgenda returns Left for invalid_external_Id_in"):
    val resultElem: Result[Elem] = FileIO.load(invalid_Availability_in);

    val result = resultElem.fold(
      error => Left(error),
      elem => AgendaIO.loadAgenda(elem)
    )

    result match
      case Right(agenda) => fail("The externalID format is wrong.")
      case Left(error) => succeed

  test("loadAgenda returns Left for invalid_external_Name_in"):
    val resultElem: Result[Elem] = FileIO.load(invalid_external_Name_in);

    val result = resultElem.fold(
      _ => false,
      elem => AgendaIO.loadAgenda(elem)
    )

    result match
      case Right(agenda) => fail("The external name format is wrong.")
      case Left(error) => succeed

  test("loadAgenda returns Left for invalid_externalDuplicate_in"):
    val resultElem: Result[Elem] = FileIO.load(invalid_externalDuplicate_in);
    val result = resultElem.fold(
      error => Left(error),
      elem => AgendaIO.loadAgenda(elem)
    )

    result match
      case Right(agenda) => fail("The availability format is wrong.")
      case Left(error) => succeed

  test("loadAgenda returns Left for invalid_student_in"):
    val resultElem: Result[Elem] = FileIO.load(invalid_student_in);

    val result = resultElem.fold(
      error => Left(error),
      elem => AgendaIO.loadAgenda(elem)
    )

    result match
      case Right(agenda) => fail("The studandId format is wrong.")
      case Left(error) => succeed

  test("loadAgenda returns Left for invalid_teacher_Id_in"):
    val resultElem: Result[Elem] = FileIO.load(invalid_teacher_Id_in);

    val result = resultElem.fold(
      error => Left(error),
      elem => AgendaIO.loadAgenda(elem)
    )

    result match
      case Right(agenda) => fail("The externalID format is wrong.")
      case Left(error) => succeed