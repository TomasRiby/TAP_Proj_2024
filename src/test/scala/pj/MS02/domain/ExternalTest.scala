package pj.MS02.domain

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.MS02.domain.AvailabilityTest.generateAvailabilityList
import pj.MS02.opaque.IDTest.{generateExternalID, generateID, generateTeacherID}
import pj.MS02.opaque.NameTest.generateName
import pj.domain.{External, Teacher}

object ExternalTest
  extends Properties("External Test"):
  def generateExternal: Gen[External] =
    for
      id <- generateExternalID
      name <- generateName
      availability <- generateAvailabilityList
    yield External.from(id, name, availability)

  property("Testing External") = forAll(generateExternal):
    _.isValid