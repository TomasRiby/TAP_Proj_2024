package pj.MS02.handleAlgorithm.ComplexGenerator

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.MS02.domain.AvailabilityTest.makeNonOverlapping
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.OTimeTest.{generateTime, generateTimeForDay}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.PreferenceTest.generatePreference
import pj.domain.Availability
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.OTime.OTime

import java.time.{LocalDate, LocalDateTime, ZoneOffset}

object AvailabilityGenerators
  extends Properties("Availability Test"):


  def generateAvailabilityListForADay(time: LocalDate): Gen[List[Availability]] =
    for {
      listLength <- Gen.choose(10, 20)
      availabilities <- Gen.listOfN(listLength, generateAvailabilityFromDay(time))
    } yield makeNonOverlapping(availabilities, listLength)


  def generateAvailabilityFromDay(time: LocalDate): Gen[Availability] =
    for {
      start <- generateTimeForDay(time)
      duration <- Gen.choose(7200, 21600) // duration between 2 hour and 6 hours in seconds
      end = start.plusSeconds(duration)
      preference <- generatePreference
      resAvailability = Availability.fromCheck(start, end, preference)
      validAvailability <- resAvailability match
        case Left(_) => Gen.fail
        case Right(valid) => Gen.const(valid)
    } yield validAvailability



  property("Testing Non-Overlapping Availability List for a day") = forAll(generateAvailabilityListForADay(LocalDate.of(2023, 5, 17))) { list =>
    println(list)
    list.zip(list.drop(1)).forall { case (a, b) => !Availability.intersects(a, b) }
  }
