package pj.MS02.domain

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.OTimeTest.{generateTime, generateTimeForDay}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.PreferenceTest.generatePreference
import pj.domain.Availability
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.OTime.OTime

import java.time.{LocalDate, LocalDateTime, ZoneOffset}

object AvailabilityTest
  extends Properties("Availability Test"):

  def generateNonOverlappingAvailabilityList: Gen[List[Availability]] =
    for {
      listLength <- Gen.choose(1, 5)
      availabilities <- Gen.listOfN(listLength, generateAvailability)
    } yield makeNonOverlapping(availabilities, listLength)

  def generateAvailability: Gen[Availability] =
    for {
      start <- generateTime
      duration <- Gen.choose(3600, 10800) // duration between 1 hour and 3 hours in seconds
      end = start.plusSeconds(duration)
      preference <- generatePreference
      resAvailability = Availability.fromCheck(start, end, preference)
      validAvailability <- resAvailability match
        case Left(_) => Gen.fail
        case Right(valid) => Gen.const(valid)
    } yield validAvailability
  

  def makeNonOverlapping(availabilities: List[Availability], desiredLength: Int): List[Availability] =
    availabilities.sortBy(_.start).foldLeft(List.empty[Availability]) { (acc, avail) =>
      acc match
        case Nil => List(avail)
        case head :: tail =>
          if (avail.start.isAfter(head.end) || avail.start.isEqual(head.end)) avail :: acc
          else
            // Adjust overlapping interval to fit right after the previous interval
            val adjustedStart = head.end.plusSeconds(1)
            val adjustedEnd = adjustedStart.plusSeconds(avail.end.toLocalDateTime.toEpochSecond(ZoneOffset.UTC) - avail.start.toLocalDateTime.toEpochSecond(ZoneOffset.UTC))
            val adjustedAvail = Availability.fromCheck(adjustedStart, adjustedEnd, avail.preference) match
              case Right(valid) => valid
              case Left(_) => head // fallback to keep the previous availability if adjustment fails
            adjustedAvail :: acc
    }.reverse.take(desiredLength)

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
