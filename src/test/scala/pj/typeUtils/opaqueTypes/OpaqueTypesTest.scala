package pj.typeUtils.opaqueTypes

import org.apache.commons.beanutils.PropertyUtils.describe
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, Name, Preference, Time}
import pj.domain.{Availability, DomainError, Resource, Teacher}
import pj.typeUtils.opaqueTypes.opaqueTypes.Preference.createPreference
import pj.typeUtils.opaqueTypes.opaqueTypes.Time.createTime

import scala.sys.SystemProperties.headless.option

class OpaqueTypesTest extends AnyFunSuite with Matchers:

  test("should create a valid ID"):
    val validIdString = "T001"
    val idResult = ID.createRegularId(validIdString)
    assert(idResult.fold(_ => false, _ => true))

  test("should create a teacher ID"):
    val validIdString = "T001"
    val idResult = ID.createTeacherId(validIdString)
    assert(idResult.fold(_ => false, _ => true))

  test("should throw error for invalid teacher ID"):
    val validIdString = "X001"
    val idResult = ID.createTeacherId(validIdString)

    idResult match
      case Left(DomainError.WrongFormat(message)) =>
        assert(message.contains(s"Teacher´s ID 'X001' should be in the *T001* format"))
      case _ =>
        fail("Unexpected error type received")

  test("should fail to create a invalid ID"):
    val invalidIdString = "invalid001"
    val idResult = ID.createRegularId(invalidIdString)

    idResult match
      case Left(DomainError.WrongFormat(message)) =>
        assert(message.contains("ID 'invalid001' is in incorrect format"))
      case _ =>
        fail("Unexpected error type received")


//////// NAME

  test("should create a valid Name"):
    val validName = "Test"
    val nameRes = Name.createName(validName)
    assert(nameRes.fold(_ => false, _ => true))

  test("should NOT create a invalid Name"):
    val validName = "Testdasd/6556189"
    val nameRes = Name.createName(validName)
    assert(nameRes.fold(_ => true, _ => false))


//////// PREFERENCE

  test("should create a valid preference"):
    val validPreference = 1
    val prefRes = Preference.createPreference(validPreference)
    assert(prefRes.fold(_ => false, _ => true))

  test("should NOT create a invalid preference"):
    val validPreference = 99
    val prefRes = Preference.createPreference(validPreference)
    assert(prefRes.fold(_ => true, _ => false))
