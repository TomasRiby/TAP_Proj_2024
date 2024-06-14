package pj.newtest.domain

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.domain.President
import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.ID


trait PresidentTestSetup extends BeforeAndAfterEach:
  this: AnyFlatSpec with Matchers =>

  def validTeacherId: ID = ID.createTeacherId("T001").getOrElse(fail("Failed to create ID"))
  def invalidTeacherId: ID = ID.createTeacherId("invalid").getOrElse(ID.createTeacherId("T999").getOrElse(fail("Failed to create ID")))

class PresidentTest extends AnyFlatSpec with Matchers with PresidentTestSetup:

  "from" should "create a President with a valid teacher ID" in:
    val president = President.from(validTeacherId)

    president.id should be(validTeacherId)

  "isValid" should "return true for President with a valid teacher ID" in:
    val president = President.from(validTeacherId)

    president.isValid should be(true)
