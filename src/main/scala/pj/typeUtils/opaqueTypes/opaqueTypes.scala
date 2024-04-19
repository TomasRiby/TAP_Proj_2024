package pj.typeUtils.opaqueTypes

import pj.domain.DomainError.WrongFormat
import pj.domain.{DomainError, External, Result, Teacher}
import pj.typeUtils.opaqueTypes.opaqueTypes.Time.timePattern

import java.time.{Duration, LocalDateTime}
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex

object opaqueTypes:
  opaque type ID = String
  opaque type Name = String
  opaque type Time = LocalDateTime
  opaque type Preference = Int
  opaque type ODuration = Duration

  object ID:
    private val teacherIdPattern: Regex = "^T[0-9]{3}$".r
    private val externalIdPattern: Regex = "^E[0-9]{3}$".r

    def createRegularId(id: String): Either[DomainError, ID] =
      id match
        case teacherIdPattern() => Right(id)
        case externalIdPattern() => Right(id)
        case _ => Left(DomainError.WrongFormat(s"ID '$id' is in incorrect format"))

    def verifyId(resourceList: List[Teacher | External]): Result[Boolean] =
      val idList = resourceList.map:
        case teacher: Teacher => teacher.id
        case external: External => external.id
      val idSet = idList.toSet
      if idList.size != idSet.size then
        Left(DomainError.DuplicateError(s"Duplicate IDs found in the $idList"))
      else
        Right(true)


    def createTeacherId(id: String): Result[ID] =
      id match
        case teacherIdPattern() => Right(id)
        case _ => Left(DomainError.WrongFormat(s"Teacher´s ID '$id' should be in the *T001* format"))

    def createExternalId(id: String): Result[ID] =
      id match
        case externalIdPattern() => Right(id)
        case _ => Left(DomainError.WrongFormat(s"External´s ID '$id' should be in the *E001* format"))


  object Name:
    private val validNamePattern: Regex = "^[a-zA-Z0-9 ]+$".r

    def createName(name: String): Result[Name] =
      name match
        case validNamePattern() => Right(name)
        case _ => Left(DomainError.WrongFormat(s"Name '$name' is in the wrong format."))


  object Time:
    private val timePattern = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    implicit val timeOrdering: Ordering[Time] = Ordering.fromLessThan[Time]((t1, t2) => t1.isBefore(t2))

    def createTime(time: String): Result[Time] =
      Try(LocalDateTime.parse(time, timePattern)) match
        case Success(parsedTime) => Right(parsedTime)
        case Failure(_) => Left(DomainError.WrongFormat(s"Time '$time' is in the wrong format. Expected ISO-8601 format."))

    extension (t: Time)
      def isAfter(other: Time): Boolean = t.isAfter(other)
      def isBefore(other: Time): Boolean = t.isBefore(other)
      def plusDays(days: Long): Time = t.plusDays(days)
      def minusHours(hours: Long): Time = t.minusHours(hours)
      def toTemporal: Temporal = t: LocalDateTime
      def toLocalDateT: LocalDateTime = t: Time


  object ODuration:
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

    extension (t: ODuration)
      def toDuration: Duration = t: ODuration


  object Preference:
    private val preferencePattern: Regex = "^[1-5]$".r

    def createPreference(preference: Int): Result[Preference] =
      preference.toString match
        case preferencePattern() => Right(preference)
        case _ => Left(DomainError.InvalidPreference(s"$preference"))

    def maxPreference(p1: Preference, p2: Preference): Preference =
      if (p1 > p2) p1 else p2

    def add(first: Preference, second: Preference, third: Preference): Preference =
      first + second + third

    def toInt(p: Preference): Int = p

    // Métodos de comparação usando a conversão implícita
    extension (p: Preference)
      def >=(other: Preference): Boolean = toInt(p) >= toInt(other)
      def >(other: Preference): Boolean = toInt(p) > toInt(other)
      def <(other: Preference): Boolean = toInt(p) < toInt(other)
      def <=(other: Preference): Boolean = toInt(p) <= toInt(other)

    // Define an Ordering instance using the toInt conversion
    given Ordering[Preference] with
      def compare(x: Preference, y: Preference): Int = toInt(x) - toInt(y)