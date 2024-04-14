package pj.typeUtils.opaqueTypes

import pj.domain.{DomainError, Result}
import pj.typeUtils.opaqueTypes.opaqueTypes.Time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try
import scala.util.matching.Regex

object opaqueTypes:
  opaque type ID = String
  opaque type Name = String
  opaque type Time = LocalDateTime
  opaque type Preference = String

  object ID:
    private val teacherIdPattern: Regex = "^T[0-9]{3}$".r
    private val externalIdPattern: Regex = "^E[0-9]{3}$".r

    def createRegularId(id: String): Result[ID] =
      id match
        case teacherIdPattern() => Right(id)
        case externalIdPattern() => Right(id)
        case _ => Left(DomainError.WrongFormat(s"ID '$id' should is in the Wrong"))

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

  //OffsetDateTime and LocalDateTime
  object Time:

    def createTime(time: String): Result[Time] =
      val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
      Try(LocalDateTime.parse(time, formatter)) match
        case scala.util.Success(parsedTime) => Right(parsedTime)
        case scala.util.Failure(_) => Left(DomainError.WrongFormat(s"Time '$time' is in the wrong format. Expected ISO-8601 format."))

  object Preference:
    private val preferencePattern: Regex = "^[1-5]$".r

    def createPreference(preference: String): Result[Preference] =
      preference match
        case preferencePattern() => Right(preference)
        case _ => Left(DomainError.WrongFormat(s"Preference '$preference' should be a number between 1 and 5."))