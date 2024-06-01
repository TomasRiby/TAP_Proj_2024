package pj.opaqueTypes

import pj.domain.{DomainError, Result}

import java.time.{Duration, LocalTime}
import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex

object ODuration:
  opaque type ODuration = Duration
  private val durationPattern: Regex = """^(\d{2}):(\d{2}):(\d{2})$""".r

  def createDuration(duration: String): Result[ODuration] =
    duration match
      case durationPattern(hours, minutes, seconds) =>
        val hrs = hours.toInt
        val mins = minutes.toInt
        val secs = seconds.toInt

        if (hrs >= 24 || mins >= 60 || secs >= 60)
          Left(DomainError.WrongFormat("Hours must be less than 24, and minutes/seconds must be less than 60"))
        else
          Try(Duration.parse(s"PT${hours}H${minutes}M${seconds}S")) match
            case Success(parsedDuration) => Right(parsedDuration)
            case Failure(_) => Left(DomainError.WrongFormat(s"Duration format is incorrect for value $duration"))
      case _ => Left(DomainError.WrongFormat(s"Duration format must be HH:MM:SS"))

  def from(): ODuration = Duration.ofSeconds(3600)
  extension (t: ODuration)
    def toDuration: Duration = t: ODuration
    def toLocalTime: LocalTime = LocalTime.of(t.toHours.toInt, t.toMinutes.toInt % 60, t.toSeconds.toInt % 60)
    def isValid: Boolean = t.toSeconds >= 0 && t.toMinutes >= 0 && t.toHours >= 0
    def toSeconds: Long = t.getSeconds