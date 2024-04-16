package pj.domain

import org.scalatest.funsuite.AnyFunSuite
import pj.typeUtils.opaqueTypes.opaqueTypes.ID
import pj.domain.DomainError

class AdvisorTest extends AnyFunSuite:

  test("should create a new Advisor instance with a valid ID"):
    // Arrange
    val validIdString = "T001"
    val idResult: Result[ID] = ID.createRegularId(validIdString)

    idResult match
      case Right(expectedId) =>
        // Act
        val advisor = Advisor.from(expectedId)
        // Assert
        assert(advisor.id == expectedId)

      case Left(error) =>
        fail(s"Failed to create ID: ${error}")

  test("should handle incorrect ID format"):
    // Arrange
    val invalidIdString = "invalid001"
    val idResult: Result[ID] = ID.createRegularId(invalidIdString)

    idResult match
      case Right(_) =>
        fail("ID creation should have failed due to incorrect format, but it succeeded.")

      case Left(DomainError.WrongFormat(message)) =>
        // Assert
        assert(message.contains("ID 'invalid001' is in incorrect format"))

      case Left(_) =>
        fail("Unexpected error type received")
