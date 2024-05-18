package pj.MS02.opaque

import org.scalacheck.{Gen, Properties}
import org.scalacheck.Prop.forAll
import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.ID


object IDTest extends Properties("ID"):
  def generateID: Gen[ID] =
    for {
      prefix <- Gen.oneOf("T", "E")
      number1 <- Gen.chooseNum(0, 9)
      number2 <- Gen.chooseNum(0, 9)
      number3 <- Gen.chooseNum(0, 9)
      resID = ID.createRegularId(prefix + number1.toString + number2.toString + number3.toString)
      generatedID <- resID match
        case Left(_) => Gen.fail
        case Right(validID) => Gen.const(validID)

    } yield generatedID

  def generateTeacherID: Gen[ID] =
    for {
      number1 <- Gen.chooseNum(0, 9)
      number2 <- Gen.chooseNum(0, 9)
      number3 <- Gen.chooseNum(0, 9)
      resID = ID.createTeacherId("T" + number1.toString + number2.toString + number3.toString)
      generatedID <- resID match
        case Left(_) => Gen.fail
        case Right(validID) => Gen.const(validID)

    } yield generatedID

  def generateExternalID: Gen[ID] =
    for {
      number1 <- Gen.chooseNum(0, 9)
      number2 <- Gen.chooseNum(0, 9)
      number3 <- Gen.chooseNum(0, 9)
      resID = ID.createExternalId("E" + number1.toString + number2.toString + number3.toString)
      generatedID <- resID match
        case Left(_) => Gen.fail
        case Right(validID) => Gen.const(validID)

    } yield generatedID

  property("IDs are in the correct format") = forAll(generateID):
    _.isValid