package pj.newtest.domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.domain.Advisor
import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.ID
import pj.domain.{DomainError, Result}

class AdvisorTest extends AnyFlatSpec with Matchers:

  "from" should "create an Advisor with a valid teacher ID" in:
    val id = ID.createTeacherId("T001").getOrElse(fail("Failed to create ID"))
    val advisor = Advisor.from(id)
    advisor.id should be (id)

  it should "create an Advisor with a valid external ID" in:
    val id = ID.createExternalId("E001").getOrElse(fail("Failed to create ID"))
    val advisor = Advisor.from(id)
    advisor.id should be (id)

  "isValid" should "return true for Advisor with a valid teacher ID" in:
    val id = ID.createTeacherId("T001").getOrElse(fail("Failed to create ID"))
    val advisor = Advisor.from(id)
    advisor.isValid should be (true)

  it should "return false for Advisor with a valid external ID" in:
    val id = ID.createExternalId("E001").getOrElse(fail("Failed to create ID"))
    val advisor = Advisor.from(id)
    advisor.isValid should be (false)
