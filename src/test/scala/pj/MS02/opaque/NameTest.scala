package pj.MS02.opaque

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.opaqueTypes.Name
import Name.Name

object NameTest extends Properties("NameTest"):
  def generateName: Gen[Name] =
    for {
      numbChars <- Gen.chooseNum(1, 1)
      chars <- Gen.listOfN(numbChars, Gen.alphaChar)
      resName = Name.createName(chars.mkString)
      generatedName <- resName match
        case Left(_) => Gen.fail
        case Right(validName) => Gen.const(validName)
    } yield generatedName

  property("Name generation is working") = forAll(generateName):
    _.isValid