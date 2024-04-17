//package pj.io
//
//import org.scalatest.funsuite.AnyFunSuite
//import org.scalatest.matchers.should.Matchers.*
//
//import scala.util.{Failure, Success}
//import scala.xml.{Elem, XML}
//import pj.domain.{Agenda, Result}
//
//class AgendaIOTest extends AnyFunSuite:
//
//  val invalid_Availability_in = "files/test/ms01/invalid_Availability_in.xml"
//  val invalid_external_Id_in = "files/test/ms01/invalid_Availability_in.xml"
//  val invalid_external_Name_in = "files/test/ms01/invalid_external_Name_in.xml"
//  val invalid_externalDuplicate_in = "files/test/ms01/invalid_external_Name_in.xml"
//  val invalid_student_in = "files/test/ms01/invalid_external_Name_in.xml"
//  val invalid_teacher_Id_in = "files/test/ms01/invalid_external_Name_in.xml"
//
//  test("loadAgenda returns Left for invalid_Availability_in"):
//    Result[Elem] s = FileIO.load(invalid_Availability_in)
//    val result = AgendaIO.loadAgenda()
//
//    result match
//      case Right(agenda) => fail("The availability format is wrong.")
//      case Left(error) => succeed
//
//  test("loadAgenda returns Left for invalid_external_Id_in"):
//      val result = AgendaIO.loadAgenda(invalid_Availability_in)
//
//      result match
//        case Right(agenda) => fail("The externalID format is wrong.")
//        case Left(error) => succeed
//
//  test("loadAgenda returns Left for invalid_external_Name_in"):
//    val result = AgendaIO.loadAgenda(invalid_external_Name_in)
//
//    result match
//      case Right(agenda) => fail("The externalID format is wrong.")
//      case Left(error) => succeed
//
//  test("loadAgenda returns Left for invalid_externalDuplicate_in"):
//    val result = AgendaIO.loadAgenda(invalid_externalDuplicate_in)
//
//    result match
//      case Right(agenda) => fail("The externalID format is wrong.")
//      case Left(error) => succeed
//
//  test("loadAgenda returns Left for invalid_student_in"):
//    val result = AgendaIO.loadAgenda(invalid_student_in)
//
//    result match
//      case Right(agenda) => fail("The externalID format is wrong.")
//      case Left(error) => succeed
//
//  test("loadAgenda returns Left for invalid_teacher_Id_in"):
//    val result = AgendaIO.loadAgenda(invalid_teacher_Id_in)
//
//    result match
//      case Right(agenda) => fail("The externalID format is wrong.")
//      case Left(error) => succeed