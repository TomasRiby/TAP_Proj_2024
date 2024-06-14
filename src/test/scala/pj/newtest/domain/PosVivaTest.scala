package pj.newtest.domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.domain.*
import pj.opaqueTypes.{ID, Name}
import pj.opaqueTypes.Name.Name
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.OTime.OTime

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

trait PosVivaTestSetup:
  this: AnyFlatSpec with Matchers =>

  // Funções auxiliares para criar instâncias válidas e inválidas
  def validName(name: String): Name = Name.createName(name).getOrElse(fail(s"Failed to create Name: $name"))
  def validID(id: String): ID = ID.createTeacherId(id).getOrElse(fail(s"Failed to create ID: $id"))

  def validPresident: President = President.from(validID("T001"))
  def validAdvisor: Advisor = Advisor.from(validID("T002"))
  def validSupervisor: Supervisor = Supervisor.from(ID.createExternalId("E001").getOrElse(fail("Failed to create External ID")))
  def validCoAdvisor: CoAdvisor = CoAdvisor.from(validID("T003"))

  def validPreViva: PreViva =
    val student = validName("Student Name")
    val title = validName("Thesis Title")
    val roles = List(
      RoleLinkedWithResource.from(validPresident, validName("President Name"), List.empty),
      RoleLinkedWithResource.from(validAdvisor, validName("Advisor Name"), List.empty),
      RoleLinkedWithResource.from(validSupervisor, validName("Supervisor Name"), List.empty),
      RoleLinkedWithResource.from(validCoAdvisor, validName("CoAdvisor Name"), List.empty)
    )
    PreViva.from(student, title, roles)

class PosVivaTest extends AnyFlatSpec with Matchers with PosVivaTestSetup:

  "from" should "create a PosViva with valid inputs" in:
    val posViva = PosViva.from(
      "Student Name",
      "Thesis Title",
      "2023-06-14T10:00:00",
      "2023-06-14T12:00:00",
      3,
      "President Name",
      "Advisor Name",
      List("Supervisor Name"),
      List("CoAdvisor Name")
    )

    posViva.student should be("Student Name")
    posViva.title should be("Thesis Title")
    posViva.start should be("2023-06-14T10:00:00")
    posViva.end should be("2023-06-14T12:00:00")
    posViva.preference should be(3)
    posViva.president should be("President Name")
    posViva.advisor should be("Advisor Name")
    posViva.supervisors should be(List("Supervisor Name"))
    posViva.coAdvisors should be(List("CoAdvisor Name"))

  "chosenAvailabilityToPosViva" should "correctly transform a PreViva to a PosViva" in:
    val preViva = validPreViva
    val start = LocalDateTime.parse("2023-06-14T10:00:00")
    val end = LocalDateTime.parse("2023-06-14T12:00:00")
    val preference = 3

    val posViva = PosViva.chosenAvailabilityToPosViva(start, end, preference, preViva)

    posViva.student should be(preViva.student.toString)
    posViva.title should be(preViva.title.toString)
    posViva.start should be(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    posViva.end should be(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    posViva.preference should be(preference)
    posViva.president should be("President Name")
    posViva.advisor should be("Advisor Name")
    posViva.supervisors should be(List("Supervisor Name"))
    posViva.coAdvisors should be(List("CoAdvisor Name"))

  it should "handle missing roles in PreViva gracefully" in:
    val student = validName("Student Name")
    val title = validName("Thesis Title")
    val roles = List(
      RoleLinkedWithResource.from(validPresident, validName("President Name"), List.empty)
    )
    val preViva = PreViva.from(student, title, roles)
    val start = LocalDateTime.parse("2023-06-14T10:00:00")
    val end = LocalDateTime.parse("2023-06-14T12:00:00")
    val preference = 3

    val posViva = PosViva.chosenAvailabilityToPosViva(start, end, preference, preViva)

    posViva.student should be(preViva.student.toString)
    posViva.title should be(preViva.title.toString)
    posViva.start should be(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    posViva.end should be(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    posViva.preference should be(preference)
    posViva.president should be("President Name")
    posViva.advisor should be("")
    posViva.supervisors should be(List.empty)
    posViva.coAdvisors should be(List.empty)
