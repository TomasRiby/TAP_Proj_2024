package pj.newtest.domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach
import pj.domain.{Availability, External}
import pj.opaqueTypes.ID
import pj.opaqueTypes.Name
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name.Name
import pj.opaqueTypes.OTime
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference
import pj.opaqueTypes.Preference.Preference

import java.time.LocalDateTime

trait ExternalTestSetup extends BeforeAndAfterEach:
  this: AnyFlatSpec with Matchers =>

  def validId: ID = ID.createExternalId("E001").getOrElse(fail("Failed to create ID"))
  def invalidId: ID = ID.createExternalId("invalid").getOrElse(ID.createExternalId("E999").getOrElse(fail("Failed to create ID")))
  def validName: Name = Name.createName("John Doe").getOrElse(fail("Failed to create Name"))
  def invalidName: Name = Name.createName("Invalid#Name").getOrElse(Name.createName("Invalid#Name").getOrElse(fail("Failed to create fallback Name")))
  def validStart: OTime = OTime.createTime("2023-06-14T10:00:00").getOrElse(fail("Failed to create OTime"))
  def validEnd: OTime = OTime.createTime("2023-06-14T12:00:00").getOrElse(fail("Failed to create OTime"))
  def invalidStart: OTime = OTime.createTime("2023-06-14T12:00:00").getOrElse(fail("Failed to create OTime"))
  def invalidEnd: OTime = OTime.createTime("2023-06-14T10:00:00").getOrElse(fail("Failed to create OTime"))
  def preference: Preference = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))
  def validAvailability: List[Availability] = List(Availability.from(validStart, validEnd, preference))
  def invalidAvailability: List[Availability] = List(Availability.from(invalidStart, invalidEnd, preference))

class ExternalTest extends AnyFlatSpec with Matchers with ExternalTestSetup:

  "from" should "create an External with valid ID, name, and availability" in:
    val external = External.from(validId, validName, validAvailability)

    external.id should be(validId)
    external.name should be(validName)
    external.availability should be(validAvailability)

  "isValid" should "return true for External with valid ID, name, and availability" in:
    val external = External.from(validId, validName, validAvailability)

    external.isValid should be(true)

  it should "return false for External with invalid availability" in:
    val external = External.from(validId, validName, invalidAvailability)

    external.isValid should be(false)
