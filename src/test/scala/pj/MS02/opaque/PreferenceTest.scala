package pj.MS02.opaque

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference.Preference
import pj.opaqueTypes.{Name, OTime, Preference}

import java.time.{Month, Year}

object PreferenceTest extends Properties("PreferenceTest"):
  def generatePreference: Gen[Preference] =
    for {
      preference <- Gen.chooseNum(1,5)
      resPreference = Preference.createPreference(preference)
      result <- resPreference match
        case Left(_) => Gen.fail
        case Right(validPreference) => Gen.const(validPreference)
    } yield result
    
  property("Testing Preference") = forAll(generatePreference):
    _.isValid