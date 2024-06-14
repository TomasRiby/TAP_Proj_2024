package pj.newtest.domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.domain.{Viva, President, Advisor, Supervisor, CoAdvisor}
import pj.opaqueTypes.Name
import pj.opaqueTypes.Name.Name
import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.ID

trait VivaTestSetup:
  this: AnyFlatSpec with Matchers =>

  // Funções auxiliares para criar instâncias válidas
  def validName(name: String): Name = Name.createName(name).getOrElse(fail(s"Failed to create Name: $name"))
  def validID(id: String): ID = ID.createTeacherId(id).getOrElse(fail(s"Failed to create ID: $id"))

  def validPresident: President = President.from(validID("T001"))
  def validAdvisor: Advisor = Advisor.from(validID("T002"))
  def validSupervisor: Supervisor = Supervisor.from(ID.createExternalId("E001").getOrElse(fail("Failed to create External ID")))
  def validCoAdvisor: CoAdvisor = CoAdvisor.from(validID("T003"))

class VivaTest extends AnyFlatSpec with Matchers with VivaTestSetup:

  "from" should "create a Viva with valid student, title, president, advisor, supervisor, and coAdvisor" in:
    val student = validName("Student Name")
    val title = validName("Thesis Title")
    val president = validPresident
    val advisor = validAdvisor
    val supervisors = List(validSupervisor)
    val coAdvisors = List(validCoAdvisor)

    val viva = Viva.from(student, title, president, advisor, supervisors, coAdvisors)

    viva.student should be(student)
    viva.title should be(title)
    viva.president should be(president)
    viva.advisor should be(advisor)
    viva.supervisor should be(supervisors)
    viva.coAdvisor should be(coAdvisors)

  it should "create a Viva with empty supervisor and coAdvisor lists" in:
    val student = validName("Student Name")
    val title = validName("Thesis Title")
    val president = validPresident
    val advisor = validAdvisor

    val viva = Viva.from(student, title, president, advisor, List.empty, List.empty)

    viva.student should be(student)
    viva.title should be(title)
    viva.president should be(president)
    viva.advisor should be(advisor)
    viva.supervisor should be(List.empty)
    viva.coAdvisor should be(List.empty)
