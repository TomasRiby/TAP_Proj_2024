package pj.MS02.domain

import org.scalacheck.Prop.{exists, forAll}
import org.scalacheck.{Gen, Prop, Properties}
import pj.MS02.opaque.OTimeTest.generateTime
import pj.MS02.opaque.PreferenceTest.generatePreference
import pj.domain.Availability
import pj.opaqueTypes.ID.ID

object AvailabilityTest
  extends Properties("Availability Test"):
  def generateAvailability: Gen[Availability] =
    for {
      start <- generateTime
      end <- generateTime
      preference <- generatePreference
      resAvailability = Availability.fromCheck(start, end, preference)
      validAvailability <- resAvailability match
        case Left(_) => Gen.fail
        case Right(valid) => Gen.const(valid)
    } yield validAvailability

  def generateAvailabilityList: Gen[List[Availability]] =
    for {
      listLenght <- Gen.choose(1, 20)
      availabilityList <- Gen.listOfN(listLenght, generateAvailability)
    } yield availabilityList

  property("Testing Availability") = forAll(generateAvailability):
    _.isValid

  property("Testing Availability List") = forAll(generateAvailabilityList):
    _.forall(_.isValid)