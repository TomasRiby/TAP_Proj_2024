package pj.newtest.domain

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.domain.{External, Resource, Teacher}
import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name.Name
import pj.opaqueTypes.Name

trait ResourceTestSetup extends BeforeAndAfterEach:
  this: AnyFlatSpec with Matchers =>

  def validTeacherId: ID = ID.createTeacherId("T001").getOrElse(fail("Failed to create Teacher ID"))
  def validExternalId: ID = ID.createExternalId("E001").getOrElse(fail("Failed to create External ID"))
  def validTeacherName: Name = Name.createName("Teacher Name").getOrElse(fail("Failed to create Teacher Name"))
  def validExternalName: Name = Name.createName("External Name").getOrElse(fail("Failed to create External Name"))

  def validTeacher: Teacher = Teacher.from(validTeacherId, validTeacherName, List.empty)
  def validExternal: External = External.from(validExternalId, validExternalName, List.empty)

class ResourceTest extends AnyFlatSpec with Matchers with ResourceTestSetup:

  "from" should "create a Resource with valid teachers and externals" in:
    val teachers = List(validTeacher)
    val externals = List(validExternal)
    val resource = Resource.from(teachers, externals)

    resource.teacher should be(teachers)
    resource.external should be(externals)

  it should "create a Resource with empty teacher and external lists" in:
    val resource = Resource.from(List.empty, List.empty)

    resource.teacher should be(List.empty)
    resource.external should be(List.empty)

  it should "create a Resource with only teachers" in:
    val teachers = List(validTeacher)
    val resource = Resource.from(teachers, List.empty)

    resource.teacher should be(teachers)
    resource.external should be(List.empty)

  it should "create a Resource with only externals" in:
    val externals = List(validExternal)
    val resource = Resource.from(List.empty, externals)

    resource.teacher should be(List.empty)
    resource.external should be(externals)
