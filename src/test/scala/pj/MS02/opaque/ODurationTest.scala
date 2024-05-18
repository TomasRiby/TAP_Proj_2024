package pj.MS02.opaque

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.opaqueTypes.Name
import pj.opaqueTypes.Name.Name

object ODurationTest extends Properties("NameTest"):
  def generateDuration: Gen[Name] =
    for {
      numbChars <- Gen.chooseNum(1, 100)
      chars <- Gen.listOfN(numbChars, Gen.alphaChar)
      resName = Name.createName(chars.mkString)
      generatedName <- resName match
        case Left(_) => Gen.fail
        case Right(validName) => Gen.const(validName)
    } yield generatedName

  property("Name generation is working") = forAll(generateDuration):
    _.isValid