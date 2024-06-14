package pj.newtest.opaqueTypes

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.opaqueTypes.ODuration
import pj.domain.{DomainError, Result}

import java.time.{Duration, LocalTime}

class ODurationTest extends AnyFlatSpec with Matchers:

  "createDuration" should "return Right for valid duration format" in:
    val result = ODuration.createDuration("01:30:45")
    result should be (Right(Duration.ofHours(1).plusMinutes(30).plusSeconds(45)))

  it should "return Left for invalid duration format" in:
    val result = ODuration.createDuration("25:61:61")
    result should be (Left(DomainError.WrongFormat("Hours must be less than 24, and minutes/seconds must be less than 60")))

  it should "return Left for non-matching pattern" in:
    val result = ODuration.createDuration("invalid_format")
    result should be (Left(DomainError.WrongFormat("Duration format must be HH:MM:SS")))

  it should "return Left for empty duration" in:
    val result = ODuration.createDuration("")
    result should be (Left(DomainError.WrongFormat("Duration format must be HH:MM:SS")))

  "ODuration extension methods" should "correctly convert ODuration to Duration" in:
    val duration = ODuration.createDuration("01:30:45").getOrElse(fail("Failed to create Duration"))
    duration.toDuration should be (Duration.ofHours(1).plusMinutes(30).plusSeconds(45))

  it should "correctly convert ODuration to LocalTime" in:
    val duration = ODuration.createDuration("01:30:45").getOrElse(fail("Failed to create Duration"))
    duration.toLocalTime should be (LocalTime.of(1, 30, 45))

  it should "correctly identify valid duration" in:
    val duration = ODuration.createDuration("01:30:45").getOrElse(fail("Failed to create Duration"))
    duration.isValid should be (true)

  it should "correctly calculate the number of seconds in ODuration" in:
    val duration = ODuration.createDuration("01:30:45").getOrElse(fail("Failed to create Duration"))
    duration.toSeconds should be (5445)

  "from method" should "create a default duration of one hour" in:
    val duration = ODuration.from()
    duration.toDuration should be (Duration.ofHours(1))
