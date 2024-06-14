package pj.newtest.opaqueTypes

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.opaqueTypes.Name
import pj.domain.{DomainError, Result}

class NameSpec extends AnyFlatSpec with Matchers:

  "createName" should "return Right for valid name" in:
    val result = Name.createName("ValidName")
    result should be (Right("ValidName"))

  it should "return Left for name with invalid characters" in:
    val result = Name.createName("Invalid#Name")
    result should be (Left(DomainError.WrongFormat("Name 'Invalid#Name' is in the wrong format.")))

  it should "return Left for blank name" in:
    val result = Name.createName("   ")
    result should be (Left(DomainError.WrongFormat("Name can't be blank")))

  it should "return Left for empty name" in:
    val result = Name.createName("")
    result should be (Left(DomainError.WrongFormat("Name can't be blank")))

  "Name extension methods" should "correctly convert Name to string" in:
    val name = Name.createName("ValidName").getOrElse(fail("Failed to create Name"))
    name.NameToString should be ("ValidName")

  it should "correctly identify valid name" in:
    val name = Name.createName("ValidName").getOrElse(fail("Failed to create Name"))
    name.isValid should be (true)

  it should "correctly identify invalid name" in:
    Name.createName("Invalid#Name") match
      case Right(name) => name.isValid should be(false)
      case Left(_) => succeed // This is expected for an invalid name

  it should "correctly return the string representation of Name" in:
    val name = Name.createName("ValidName").getOrElse(fail("Failed to create Name"))
    name.toString should be ("ValidName")
