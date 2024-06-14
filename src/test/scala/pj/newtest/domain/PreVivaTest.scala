package pj.newtest.domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.domain.{President, Advisor, CoAdvisor, Supervisor, Teacher, External, RoleLinkedWithResource, PreViva, Viva}
import pj.opaqueTypes.ID
import pj.opaqueTypes.Name
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name.Name
import scala.collection.immutable.HashSet

trait PreVivaTestSetup:
  this: AnyFlatSpec with Matchers =>

  // Funções auxiliares para criar instâncias válidas e inválidas
  def validName(name: String): Name = Name.createName(name).getOrElse(fail(s"Failed to create Name: $name"))
  def validID(id: String): ID = ID.createTeacherId(id).getOrElse(fail(s"Failed to create ID: $id"))

  def validPresident: President = President.from(validID("T001"))
  def validAdvisor: Advisor = Advisor.from(validID("T002"))
  def validSupervisor: Supervisor = Supervisor.from(ID.createExternalId("E001").getOrElse(fail("Failed to create External ID")))
  def validCoAdvisor: CoAdvisor = CoAdvisor.from(validID("T003"))

  def validTeacher: Teacher = Teacher.from(validID("T001"), validName("Teacher Name"), List.empty)
  def validExternal: External = External.from(ID.createExternalId("E001").getOrElse(fail("Failed to create External ID")), validName("External Name"), List.empty)

class PreVivaTest extends AnyFlatSpec with Matchers with PreVivaTestSetup:

  "from" should "create a PreViva with valid student, title, and roleLinkedWithResourceList" in:
    val student = validName("Student Name")
    val title = validName("Thesis Title")
    val roles = List(
      RoleLinkedWithResource.from(validPresident, validName("President Name"), List.empty),
      RoleLinkedWithResource.from(validAdvisor, validName("Advisor Name"), List.empty)
    )

    val preViva = PreViva.from(student, title, roles)

    preViva.student should be(student)
    preViva.title should be(title)
    preViva.roleLinkedWithResourceList should be(roles)

  "hashSetOfIds" should "return a HashSet of IDs from the roleLinkedWithResourceList" in:
    val student = validName("Student Name")
    val title = validName("Thesis Title")
    val roles = List(
      RoleLinkedWithResource.from(validPresident, validName("President Name"), List.empty),
      RoleLinkedWithResource.from(validAdvisor, validName("Advisor Name"), List.empty),
      RoleLinkedWithResource.from(validSupervisor, validName("Supervisor Name"), List.empty),
      RoleLinkedWithResource.from(validCoAdvisor, validName("CoAdvisor Name"), List.empty)
    )

    val preViva = PreViva.from(student, title, roles)
    val ids = PreViva.hashSetOfIds(preViva)

    ids should contain(validPresident.id)
    ids should contain(validAdvisor.id)
    ids should contain(validSupervisor.id)
    ids should contain(validCoAdvisor.id)

  "linkVivaWithResource" should "link Viva with the appropriate resources" in:
    val student = validName("Student Name")
    val title = validName("Thesis Title")
    val president = validPresident
    val advisor = validAdvisor
    val supervisors = List(validSupervisor)
    val coAdvisors = List(validCoAdvisor)

    val viva = Viva.from(student, title, president, advisor, supervisors, coAdvisors)
    val teacherList = List(
      Teacher.from(president.id, validName("President Name"), List.empty),
      Teacher.from(advisor.id, validName("Advisor Name"), List.empty),
      Teacher.from(coAdvisors.head.id, validName("CoAdvisor Name"), List.empty)
    )
    val externalList = List(
      External.from(supervisors.head.id, validName("Supervisor Name"), List.empty)
    )

    val preViva = PreViva.linkVivaWithResource(viva, teacherList, externalList)

    preViva.student should be(student)
    preViva.title should be(title)
    preViva.roleLinkedWithResourceList should not be empty
    preViva.roleLinkedWithResourceList.map(_.role) should contain theSameElementsAs List(president, advisor) ++ supervisors ++ coAdvisors

  it should "create a PreViva with empty resources if no matching resources are found" in:
    val student = validName("Student Name")
    val title = validName("Thesis Title")
    val president = validPresident
    val advisor = validAdvisor
    val supervisors = List(validSupervisor)
    val coAdvisors = List(validCoAdvisor)

    val viva = Viva.from(student, title, president, advisor, supervisors, coAdvisors)
    val teacherList = List.empty[Teacher]
    val externalList = List.empty[External]

    val preViva = PreViva.linkVivaWithResource(viva, teacherList, externalList)

    preViva.student should be(student)
    preViva.title should be(title)
    preViva.roleLinkedWithResourceList should be(empty)
