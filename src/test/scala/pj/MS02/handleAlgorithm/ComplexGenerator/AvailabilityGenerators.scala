package pj.MS02.handleAlgorithm.ComplexGenerator

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.MS02.domain.AvailabilityTest.makeNonOverlapping
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.OTimeTest.{generateTime, generateTimeForDay}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.PreferenceTest.generatePreference
import pj.domain.{Availability, DomainError}
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.{OTime, Preference}
import pj.opaqueTypes.OTime.OTime

import java.time.{LocalDate, LocalDateTime, ZoneOffset}

object AvailabilityGenerators
  extends Properties("Availability Test"):


  def generateAvailabilityListForADayWithDuration(time: LocalDate, duration: ODuration, length: Int): Gen[List[Availability]] =
    for {
      availabilities <- Gen.listOfN(length, generateAvailabilityFromDayWithDuration(time, duration))
    } yield makeNonOverlapping(availabilities, length)

  def generateAvailListForADayContainingAvail(time: LocalDate, mandatoryAvailability: Availability): Gen[List[Availability]] =
    for {
      listLength <- Gen.choose(1, 1)
      additionalAvailabilities <- Gen.listOfN(listLength, generateAvailabilityFromDay(time))
      allAvailabilities = makeNonOverlappingSuper(mandatoryAvailability :: additionalAvailabilities, mandatoryAvailability, listLength + 1)
    } yield allAvailabilities

  def makeNonOverlappingSuper(availabilities: List[Availability], mandatoryAvailability: Availability, desiredLength: Int): List[Availability] =
    val nonOverlappingList = availabilities.filter(_ != mandatoryAvailability).foldLeft(List(mandatoryAvailability)) { (acc, avail) =>
      acc.headOption match
        case None => List(avail)
        case Some(head) =>
          if (avail.start.isAfter(head.end) || avail.start.isEqual(head.end)) avail :: acc
          else if (avail == mandatoryAvailability) avail :: acc // Ensure mandatory availability is always included
          else
            // Adjust overlapping interval to fit right after the previous interval
            val adjustedStart = head.end.plusSeconds(1)
            val adjustedEnd = adjustedStart.plusSeconds(avail.end.toLocalDateTime.toEpochSecond(ZoneOffset.UTC) - avail.start.toLocalDateTime.toEpochSecond(ZoneOffset.UTC))
            val adjustedAvail = Availability.fromCheck(adjustedStart, adjustedEnd, avail.preference) match
              case Right(valid) => valid
              case Left(_) => head // fallback to keep the previous availability if adjustment fails
            adjustedAvail :: acc
    }
    nonOverlappingList.reverse.take(desiredLength)

  def generateAvailabilityFromDay(time: LocalDate): Gen[Availability] =
    for {
      start <- generateTimeForDay(time)
      duration <- Gen.choose(0, 3599) // duration between 0 hour and 1 hours in seconds
      end = start.plusSeconds(duration)
      preference <- generatePreference
      resAvailability = if (end.toLocalDate.isAfter(start.toLocalDate)) Left(DomainError.WrongFormat("End time must be within the same day"))
      else Availability.fromCheck(start, end, preference)
      validAvailability <- resAvailability match
        case Left(_) => Gen.fail
        case Right(valid) => Gen.const(valid)
    } yield validAvailability

  def generateAvailabilityFromDayWithDuration(time: LocalDate, duration: ODuration): Gen[Availability] =
    for {
      start <- generateTimeForDay(time)
      end = start.plusSeconds(duration.toSeconds)
      preference <- generatePreference
      resAvailability = Availability.fromCheck(start, end, preference)
      validAvailability <- resAvailability match
        case Left(_) => Gen.fail
        case Right(valid) => Gen.const(valid)
    } yield validAvailability

  val avail = for {
    start <- OTime.createTime("2024-06-11T09:30:00")
    end <- OTime.createTime("2024-06-11T10:30:00")
    preference <- Preference.createPreference(5)
    avail <- Availability.fromCheck(start, end, preference)
  } yield avail

  avail match
    case Right(availability) => property("Testing Non-Overlapping Availability List for a day") = forAll(generateAvailListForADayContainingAvail(LocalDate.of(2024, 6, 11), availability)) { list =>
      //      println(list.map(x => x == availability).filter(x => x))
      list.zip(list.drop(1)).forall { case (a, b) => !Availability.intersects(a, b) } && list.contains(availability)
    }
  // Property test to ensure no Availability overpasses the day
  property("No Availability overpasses the day") = forAll(generateAvailabilityFromDay(LocalDate.of(2024, 6, 11))) { availability =>
    println(availability)
    availability.end.toLocalDate.isEqual(availability.start.toLocalDate)
  }





