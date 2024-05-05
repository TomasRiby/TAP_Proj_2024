package pj.domain

import scala.annotation.targetName
import scala.util.{Failure, Success, Try}

opaque type Hour = Int

object Hour:

  private def isValidHour(hour: Int): Result[Unit] =
    if (hour >= 0 && hour <= 24 && hour.toString.length <= 2) Right({})
    else Left(DomainError.InvalidHour(s"Hour must be between 0-24. $hour"))

  def from(hourString: String): Result[Hour] =
    Try(hourString.toInt) match
      case Failure(exception) => Left(DomainError.InvalidHour(s"Hour must be numeric $hourString"))
      case Success(hour) =>
        for {
          _ <- isValidHour(hour)
        } yield hour

  extension (hour: Hour)
    @targetName("Hour.to")
    def to: Int = hour

opaque type Minute = Int

object Minute:

  private def isValidMinute(minute: Int): Result[Unit] =
    if (minute >= 0 && minute <= 60 && minute.toString.length <= 2) Right({})
    else Left(DomainError.InvalidMinute(s"Minute must be between 0-60. $minute"))

  def from(minuteString: String): Result[Minute] =
    Try(minuteString.toInt) match
      case Failure(exception) => Left(DomainError.InvalidMinute(s"Minute must be numeric $minuteString"))
      case Success(minute) =>
        for {
          _ <- isValidMinute(minute)
        } yield minute

  extension(minute: Minute)
    @targetName("Minute.to")
    def to: Int = minute


opaque type Second = Int

object Second:
  private def isValidSecond(second: Int): Result[Unit] =
    if (second >= 0 && second <= 60 && second.toString.length <= 2) Right({})
    else Left(DomainError.InvalidSecond(s"Second must be between 0-60. $second"))

  def from(secondString: String): Result[Second] =
    Try(secondString.toInt) match
      case Failure(exception) => Left(DomainError.InvalidSecond(s"Second must be numeric $secondString"))
      case Success(second) =>
        for {
          _ <- isValidSecond(second)
        } yield second

  extension (second: Second)
    @targetName("Second.to")
    def to: Int = second

final case class Time(hour: Hour, minutes: Minute, seconds: Second)

object Time:

  def from(durationString: String): Result[Time] =
    for {
      durationParts <- splitDuration(durationString)
      hour <- Hour.from(durationParts(0))
      minute <- Minute.from(durationParts(1))
      second <- Second.from(durationParts(2))
    } yield Time(hour, minute, second)

  private def splitDuration(durationString: String): Result[Array[String]] =
    val durationParts = durationString.split(':')
    if (durationParts.length == 3) Right(durationParts)
    else Left(DomainError.InvalidDuration(s"$durationString. Follow the format hh:mm:ss"))