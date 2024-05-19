package pj.MS02.handleAlgorithm

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.MS02.domain.AvailabilityTest.{generateAvailability, generateAvailabilityList, generateNonOverlappingAvailabilityList}
import pj.MS02.handleAlgorithm.ComplexGenerator.ResourceTest.{generateExternal, generateTeacher}
import pj.MS02.handleAlgorithm.ComplexGenerator.VivaTest.*
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.IDTest.generateTeacherID
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.NameTest.generateName
import pj.domain.Availability
import pj.domain.Availability.updateAvailabilitySlots
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.{ID, ODuration}

import java.time.Duration

object updateAvailabilitySlotsTest extends Properties("updateAvailabilitySlotsTest"):

  // Helper method to create ODuration from hours, minutes, and seconds
  def createODuration(hours: Int, minutes: Int, seconds: Int): Option[ODuration] =
    val durationString = f"$hours%02d:$minutes%02d:$seconds%02d"
    ODuration.createDuration(durationString) match
      case Right(duration) => Some(duration)
      case Left(_) => None

  // Generate non-empty list of Availability
  val nonEmptyAvailabilityList: Gen[List[Availability]] = Gen.nonEmptyListOf(generateAvailability)

  // Generate ODuration
  val generateDuration: Gen[ODuration] = for {
    hours <- Gen.choose(0, 23)
    minutes <- Gen.choose(0, 59)
    seconds <- Gen.choose(0, 59)
    durationOpt = createODuration(hours, minutes, seconds)
    duration <- durationOpt.fold[Gen[ODuration]](Gen.fail)(Gen.const)
  } yield duration

  // Generate non-empty list of used slots
  val nonEmptyUsedSlotsList: Gen[List[Availability]] = Gen.nonEmptyListOf(generateAvailability)

  property("updateAvailabilitySlots should correctly update slots based on used slots") = forAll(
    nonEmptyAvailabilityList,
    generateDuration,
    nonEmptyUsedSlotsList
  ) { (availabilities, duration, usedSlots) =>
    val updatedSlots = updateAvailabilitySlots(availabilities, duration, usedSlots)

    // Debug prints
    println(s"Availabilities: $availabilities")
    println(s"Duration: $duration")
    println(s"Used Slots: $usedSlots")
    println(s"Updated Slots: $updatedSlots")

    // Ensure no overlaps in updated slots
    val noOverlap = updatedSlots.combinations(2).forall:
      case List(a, b) =>
        a.end.isBefore(b.start) || b.end.isBefore(a.start)

    // Ensure all updated slots meet the minimum duration requirement
    val validDuration = updatedSlots.forall { slot =>
      java.time.Duration.between(slot.start.toTemporal, slot.end.toTemporal).compareTo(duration.toDuration) >= 0
    }

    // Ensure all used slots are reflected in the updated slots
    val allSlotsUsed = usedSlots.forall { usedSlot =>
      updatedSlots.exists { updatedSlot =>
        !updatedSlot.end.isBefore(usedSlot.start) && !updatedSlot.start.isAfter(usedSlot.end)
      }
    }

    // Ensure original slots are included when there are no used slots
    val originalSlotsIncludedWhenNoUsedSlots = if (usedSlots.isEmpty)
      updatedSlots.toSet == availabilities.toSet
    else
      true

    // Ensure all availabilities are considered
    val allAvailabilitiesConsidered = availabilities.forall { availability =>
      updatedSlots.exists { updatedSlot =>
        !updatedSlot.end.isBefore(availability.start) && !updatedSlot.start.isAfter(availability.end)
      }
    }

    noOverlap && validDuration && allSlotsUsed && originalSlotsIncludedWhenNoUsedSlots && allAvailabilitiesConsidered
  }
