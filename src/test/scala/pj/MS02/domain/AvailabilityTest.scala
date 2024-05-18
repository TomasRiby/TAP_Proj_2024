package pj.MS02.domain

import org.scalacheck.Prop.{exists, forAll}
import org.scalacheck.{Gen, Prop, Properties}
import pj.MS02.opaque.OTimeTest.generateTime
import pj.MS02.opaque.PreferenceTest.generatePreference
import pj.domain.Availability
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.OTime.OTime

object AvailabilityTest
  extends Properties("Availability Test"):

  def generateNonOverlappingAvailabilityList: Gen[List[Availability]] =
    for {
      listLength <- Gen.choose(1, 20)
      availabilities <- Gen.listOfN(listLength, generateAvailability)
    } yield makeNonOverlapping(availabilities)

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

  def makeNonOverlapping(availabilities: List[Availability]): List[Availability] =
    availabilities.sortBy(_.start).foldLeft(List.empty[Availability]) { (acc, avail) =>
      acc match
        case Nil => List(avail)
        case head :: _ =>
          if (avail.start.isAfter(head.end) || avail.start.isEqual(head.end)) avail :: acc
          else acc
    }.reverse

  property("Testing Non-Overlapping Availability List") = forAll(generateNonOverlappingAvailabilityList) { list =>
    list.zip(list.drop(1)).forall { case (a, b) => b.start.isAfter(a.end) || b.start.isEqual(a.end) }
  }

  property("Testing Availability") = forAll(generateAvailability) { avail =>
    avail.isValid
  }

  property("Testing Availability List") = forAll(generateAvailabilityList) { list =>
    list.forall(_.isValid)
  }

  // Ensuring existing tests use the new non-overlapping list generator
  def generateAvailabilityList: Gen[List[Availability]] =
    generateNonOverlappingAvailabilityList
