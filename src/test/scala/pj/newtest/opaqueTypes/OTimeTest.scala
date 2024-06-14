package pj.newtest.opaqueTypes

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.opaqueTypes.OTime
import pj.domain.{DomainError, Result}

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal

class OTimeSpec extends AnyFlatSpec with Matchers:

  "createTime" should "return Right for valid ISO-8601 format time string" in:
    val result = OTime.createTime("2023-06-14T10:15:30")
    result should be (Right(LocalDateTime.parse("2023-06-14T10:15:30")))

  it should "return Left for invalid time format string" in:
    val result = OTime.createTime("14-06-2023 10:15:30")
    result should be (Left(DomainError.WrongFormat("Time '14-06-2023 10:15:30' is in the wrong format. Expected ISO-8601 format.")))

  it should "return Right for valid LocalDateTime object" in:
    val dateTime = LocalDateTime.of(2023, 6, 14, 10, 15, 30)
    val result = OTime.createTime(dateTime)
    result should be (Right(dateTime))

  "OTime extension methods" should "correctly identify if one time is after another" in:
    val time1 = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val time2 = OTime.createTime("2023-06-14T09:15:30").getOrElse(fail("Failed to create OTime"))
    time1.isAfter(time2) should be (true)

  it should "correctly identify if one time is equal to another" in:
    val time1 = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val time2 = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    time1.isEqual(time2) should be (true)

  it should "correctly identify if one time is before another" in:
    val time1 = OTime.createTime("2023-06-14T09:15:30").getOrElse(fail("Failed to create OTime"))
    val time2 = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    time1.isBefore(time2) should be (true)

  it should "correctly add days to a time" in:
    val time = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val newTime = time.plusDays(2)
    newTime should be (LocalDateTime.parse("2023-06-16T10:15:30"))

  it should "correctly subtract hours from a time" in:
    val time = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val newTime = time.minusHours(2)
    newTime should be (LocalDateTime.parse("2023-06-14T08:15:30"))

  it should "correctly convert OTime to Temporal" in:
    val time = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    time.toTemporal should be (LocalDateTime.parse("2023-06-14T10:15:30"))

  it should "correctly convert OTime to LocalDateTime" in:
    val time = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    time.toLocalDateTime should be (LocalDateTime.parse("2023-06-14T10:15:30"))

  it should "correctly convert OTime to LocalDate" in:
    val time = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    time.toLocalDate should be (LocalDate.parse("2023-06-14"))

  it should "correctly identify valid OTime" in:
    val time = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    time.isValid should be (true)

  it should "correctly add seconds to a time" in:
    val time = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val newTime = time.plusSeconds(60)
    newTime should be (LocalDateTime.parse("2023-06-14T10:16:30"))
