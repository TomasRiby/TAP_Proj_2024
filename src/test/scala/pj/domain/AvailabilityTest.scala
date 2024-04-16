package pj.domain

import pj.typeUtils.opaqueTypes.opaqueTypes
import pj.typeUtils.opaqueTypes.opaqueTypes.Time.createTime
import pj.typeUtils.opaqueTypes.opaqueTypes.Preference.createPreference
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import scala.util.{Success, Failure}

class AvailabilityTest extends AnyFunSuite with Matchers:

  test("Availability from method should create an Availability object with correct opaque types"):
    val startTime = createTime("2022-05-18T15:00:00")
    val endTime = createTime("2022-05-18T16:00:00")
    val preference = createPreference("3")

    (startTime, endTime, preference) match
      case (Right(start), Right(end), Right(pref)) =>
        val result = Availability.from(start, end, pref)
        assert(result.start == start)
        assert(result.end == end)
        assert(result.preference == pref)
      case _ => assert(false, "Invalid input for time or preference")

  test("Check createTime and createPreference functions"):
    val startTime = createTime("invalid-date")
    val preference = createPreference("invalid-preference")

    assert(startTime.isLeft, "Expected failure when parsing invalid date time")
    assert(preference.isLeft, "Expected failure when creating preference from invalid input")

