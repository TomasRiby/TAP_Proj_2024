package pj.newtest.opaqueTypes

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.opaqueTypes.ID
import pj.domain.{DomainError, External, Teacher, Result}
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name
import pj.domain.Availability

class IDTest extends AnyFlatSpec with Matchers:

  "createRegularId" should "return Right for valid teacher ID" in:
    val result = ID.createRegularId("T001")
    result should be (Right("T001"))

  it should "return Right for valid external ID" in:
    val result = ID.createRegularId("E001")
    result should be (Right("E001"))

  it should "return Left for invalid ID" in:
    val result = ID.createRegularId("X001")
    result should be (Left(DomainError.WrongFormat("ID 'X001' is in incorrect format")))

  "createTeacherId" should "return Right for valid teacher ID" in:
    val result = ID.createTeacherId("T001")
    result should be (Right("T001"))

  it should "return Left for invalid teacher ID" in:
    val result = ID.createTeacherId("E001")
    result should be (Left(DomainError.WrongFormat("Teacher´s ID 'E001' should be in the *T001* format")))

  "createExternalId" should "return Right for valid external ID" in:
    val result = ID.createExternalId("E001")
    result should be (Right("E001"))

  it should "return Left for invalid external ID" in:
    val result = ID.createExternalId("T001")
    result should be (Left(DomainError.WrongFormat("External´s ID 'T001' should be in the *E001* format")))

  "verifyId" should "return Right(true) for unique IDs" in:
    val teacherId1 = ID.createTeacherId("T001").getOrElse(fail("Failed to create ID"))
    val teacherId2 = ID.createTeacherId("T002").getOrElse(fail("Failed to create ID"))
    val externalId1 = ID.createExternalId("E001").getOrElse(fail("Failed to create ID"))
    val externalId2 = ID.createExternalId("E002").getOrElse(fail("Failed to create ID"))

    val name1 = Name.createName("Teacher1").getOrElse(fail("Failed to create Name"))
    val name2 = Name.createName("Teacher2").getOrElse(fail("Failed to create Name"))
    val name3 = Name.createName("External1").getOrElse(fail("Failed to create Name"))
    val name4 = Name.createName("External2").getOrElse(fail("Failed to create Name"))

    val teachers = List(
      Teacher.from(teacherId1, name1, List.empty[Availability]),
      Teacher.from(teacherId2, name2, List.empty[Availability])
    )
    val externals = List(
      External.from(externalId1, name3, List.empty[Availability]),
      External.from(externalId2, name4, List.empty[Availability])
    )
    val resources = teachers ++ externals
    val result = ID.verifyId(resources)
    result should be (Right(true))

  it should "return Left for duplicate IDs" in:
    val teacherId1 = ID.createTeacherId("T001").getOrElse(fail("Failed to create ID"))
    val name1 = Name.createName("Teacher1").getOrElse(fail("Failed to create Name"))
    val name2 = Name.createName("Teacher2").getOrElse(fail("Failed to create Name"))

    val teachers = List(
      Teacher.from(teacherId1, name1, List.empty[Availability]),
      Teacher.from(teacherId1, name2, List.empty[Availability])
    )
    val result = ID.verifyId(teachers)
    result should be (Left(DomainError.DuplicateError("Duplicate IDs found in the List(T001, T001)")))

  "ID extension methods" should "correctly identify valid teacher ID" in:
    val id: ID = ID.createTeacherId("T001").getOrElse(fail("Failed to create ID"))
    id.isTeacherId should be (true)
    id.isExternalId should be (false)
    id.isValid should be (true)

  it should "correctly identify valid external ID" in:
    val id: ID = ID.createExternalId("E001").getOrElse(fail("Failed to create ID"))
    id.isTeacherId should be (false)
    id.isExternalId should be (true)
    id.isValid should be (true)

  it should "correctly convert ID to string" in:
    val id: ID = ID.createTeacherId("T001").getOrElse(fail("Failed to create ID"))
    id.IDtoString should be ("T001")
