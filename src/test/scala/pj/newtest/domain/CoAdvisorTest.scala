package pj.newtest.domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.domain.CoAdvisor
import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.ID
import pj.domain.DomainError

class CoAdvisorTest extends AnyFlatSpec with Matchers:

  "from" should "create a CoAdvisor with a valid teacher ID" in:
    val id = ID.createTeacherId("T001").getOrElse(fail("Failed to create ID"))
    val coAdvisor = CoAdvisor.from(id)
    coAdvisor.id should be (id)

  it should "create a CoAdvisor with a valid external ID" in:
    val id = ID.createExternalId("E001").getOrElse(fail("Failed to create ID"))
    val coAdvisor = CoAdvisor.from(id)
    coAdvisor.id should be (id)

  "isValid" should "return true for CoAdvisor with a valid teacher ID" in:
    val id = ID.createTeacherId("T001").getOrElse(fail("Failed to create ID"))
    val coAdvisor = CoAdvisor.from(id)
    coAdvisor.isValid should be (true)

  it should "return true for CoAdvisor with a valid external ID" in:
    val id = ID.createExternalId("E001").getOrElse(fail("Failed to create ID"))
    val coAdvisor = CoAdvisor.from(id)
    coAdvisor.isValid should be (true)
