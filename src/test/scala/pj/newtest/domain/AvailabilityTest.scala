package pj.newtest.domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.domain.{Availability, DomainError}
import pj.opaqueTypes.{OTime, Preference, ODuration}
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference.Preference
import pj.opaqueTypes.ODuration.ODuration
import java.time.LocalDateTime

class AvailabilityTest extends AnyFlatSpec with Matchers:

  "from" should "create an Availability with valid OTime, OTime, and Preference" in:
    val start = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val end = OTime.createTime("2023-06-14T12:15:30").getOrElse(fail("Failed to create OTime"))
    val preference = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val availability = Availability.from(start, end, preference)

    availability.start should be(start)
    availability.end should be(end)
    availability.preference should be(preference)

  "fromCheck" should "return Right for valid start and end times" in:
    val start = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val end = OTime.createTime("2023-06-14T12:15:30").getOrElse(fail("Failed to create OTime"))
    val preference = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val result = Availability.fromCheck(start, end, preference)

    result should be(Right(Availability.from(start, end, preference)))

  it should "return Left for invalid start and end times" in:
    val start = OTime.createTime("2023-06-14T12:15:30").getOrElse(fail("Failed to create OTime"))
    val end = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val preference = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val result = Availability.fromCheck(start, end, preference)

    result should be(Left(DomainError.WrongFormat("End time must be after start time")))

  "isValid" should "return true for valid Availability" in:
    val start = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val end = OTime.createTime("2023-06-14T12:15:30").getOrElse(fail("Failed to create OTime"))
    val preference = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val availability = Availability.from(start, end, preference)

    availability.isValid should be(true)

  it should "return false for invalid Availability with start time after end time" in:
    val start = OTime.createTime("2023-06-14T12:15:30").getOrElse(fail("Failed to create OTime"))
    val end = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val preference = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val availability = Availability.from(start, end, preference)

    availability.isValid should be(false)

  "intersects" should "return true for overlapping availabilities" in:
    val start1 = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val end1 = OTime.createTime("2023-06-14T12:15:30").getOrElse(fail("Failed to create OTime"))
    val preference1 = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val start2 = OTime.createTime("2023-06-14T11:00:00").getOrElse(fail("Failed to create OTime"))
    val end2 = OTime.createTime("2023-06-14T13:00:00").getOrElse(fail("Failed to create OTime"))
    val preference2 = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val availability1 = Availability.from(start1, end1, preference1)
    val availability2 = Availability.from(start2, end2, preference2)

    Availability.intersects(availability1, availability2) should be(true)

  it should "return false for non-overlapping availabilities" in:
    val start1 = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val end1 = OTime.createTime("2023-06-14T11:15:30").getOrElse(fail("Failed to create OTime"))
    val preference1 = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val start2 = OTime.createTime("2023-06-14T12:00:00").getOrElse(fail("Failed to create OTime"))
    val end2 = OTime.createTime("2023-06-14T13:00:00").getOrElse(fail("Failed to create OTime"))
    val preference2 = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val availability1 = Availability.from(start1, end1, preference1)
    val availability2 = Availability.from(start2, end2, preference2)

    Availability.intersects(availability1, availability2) should be(false)
