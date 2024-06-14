package pj.newtest.domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import pj.domain.Supervisor
import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.ID

class SupervisorTest extends AnyFunSuite with Matchers:

  def validExternalId: ID = ID.createExternalId("E001").getOrElse(fail("Failed to create External ID"))
  def invalidExternalId: ID = ID.createExternalId("invalid").getOrElse(ID.createExternalId("E999").getOrElse(fail("Failed to create External ID")))

  test("Supervisor should be created with a valid external ID"):
    val supervisor = Supervisor.from(validExternalId)
    supervisor.id should be(validExternalId)

  test("isValid should return true for Supervisor with a valid external ID"):
    val supervisor = Supervisor.from(validExternalId)
    supervisor.isValid should be(true)

