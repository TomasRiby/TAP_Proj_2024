package pj.opaqueTypes

import pj.domain.{DomainError, Result}

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import scala.util.{Failure, Success, Try}

object OTime:
  opaque type OTime = LocalDateTime
  private val timePattern = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  implicit val timeOrdering: Ordering[OTime] = Ordering.fromLessThan[OTime]((t1, t2) => t1.isBefore(t2))

  def createTime(time: String): Result[OTime] =
    Try(LocalDateTime.parse(time)) match
      case Failure(exception) => Left(DomainError.WrongFormat(s"Time '$time' is in the wrong format. Expected ISO-8601 format."))
      case Success(value) => Right(value)

  def createTime(dateTime: LocalDateTime): Result[OTime] =
    Right(dateTime)


  extension (t: OTime)
    def isAfter(other: OTime): Boolean = t.isAfter(other)
    def isEqual(other: OTime): Boolean = t.isEqual(other)
    def isBefore(other: OTime): Boolean = t.isBefore(other)
    def plusDays(days: Long): OTime = t.plusDays(days)
    def minusHours(hours: Long): OTime = t.minusHours(hours)
    def toTemporal: Temporal = t: LocalDateTime
    def toLocalDateTime: LocalDateTime = t: OTime
    def toLocalDate: LocalDate = t.toLocalDate
    def isValid: Boolean = if LocalDateTime.parse(t.toString, timePattern) != null then true else false
    def plusSeconds(seconds: Long): OTime = t.plusSeconds(seconds)
   
