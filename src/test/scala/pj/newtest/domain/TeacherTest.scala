package pj.newtest.domain

import org.scalatest.Assertions.fail
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.domain.{Availability, Teacher}
import pj.opaqueTypes.ID
import pj.opaqueTypes.Name
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name.Name
import pj.opaqueTypes.OTime
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference
import pj.opaqueTypes.Preference.Preference

import java.time.LocalDateTime

trait TeacherTestSetup:
  def validTeacherId: ID = ID.createTeacherId("T001").getOrElse(fail("Failed to create Teacher ID"))
  def invalidTeacherId: ID = ID.createTeacherId("invalid").getOrElse(ID.createTeacherId("T999").getOrElse(fail("Failed to create Teacher ID")))
  def validName: Name = Name.createName("Teacher Name").getOrElse(fail("Failed to create Name"))
  def invalidName: Name = Name.createName("Invalid#Name").getOrElse(Name.createName("Invalid#Name").getOrElse(fail("Failed to create fallback Name")))
  def validStart: OTime = OTime.createTime("2023-06-14T10:00:00").getOrElse(fail("Failed to create OTime"))
  def validEnd: OTime = OTime.createTime("2023-06-14T12:00:00").getOrElse(fail("Failed to create OTime"))
  def invalidStart: OTime = OTime.createTime("2023-06-14T12:00:00").getOrElse(fail("Failed to create OTime"))
  def invalidEnd: OTime = OTime.createTime("2023-06-14T10:00:00").getOrElse(fail("Failed to create OTime"))
  def preference: Preference = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))
  def validAvailability: List[Availability] = List(Availability.from(validStart, validEnd, preference))
  def invalidAvailability: List[Availability] = List(Availability.from(invalidStart, invalidEnd, preference))

class TeacherTest extends AnyFlatSpec with Matchers with TeacherTestSetup:

  "from" should "create a Teacher with valid ID, name, and availability" in:
    val teacher = Teacher.from(validTeacherId, validName, validAvailability)

    teacher.id should be(validTeacherId)
    teacher.name should be(validName)
    teacher.availability should be(validAvailability)

  "isValid" should "return true for Teacher with valid ID, name, and availability" in:
    val teacher = Teacher.from(validTeacherId, validName, validAvailability)

    teacher.isValid should be(true)

  it should "return false for Teacher with invalid availability" in:
    val teacher = Teacher.from(validTeacherId, validName, invalidAvailability)

    teacher.isValid should be(false)
