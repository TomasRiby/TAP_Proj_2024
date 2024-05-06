package pj.domain.myDomain

import pj.domain.{DomainError, Result}

import java.time.LocalDateTime
import scala.annotation.targetName
import scala.util.{Failure, Success, Try}

opaque type ODateTime = LocalDateTime

object ODateTime:
  def from(dateTime: String): Result[ODateTime] =
    Try(LocalDateTime.parse(dateTime)) match
      case Failure(exception) => Left(DomainError.InvalidDateTime(s"$dateTime - Date should follow the format yyyy-mm-ddThh:mm:ss"))
      case Success(value) => Right(value)

  def from(dateTime: LocalDateTime): Result[ODateTime] =
    Right(dateTime)

  extension (dateTime: ODateTime)
    @targetName("DateTime.to")
    def to: LocalDateTime = dateTime

    @targetName("DateTime.isEqual")
    def isEqual(other: ODateTime): Boolean = dateTime.isEqual(other)

    @targetName("DateTime.isAfter")
    def isAfter(other: ODateTime): Boolean = dateTime.isAfter(other)

    @targetName("DateTime.isBefore")
    def isBefore(other: ODateTime): Boolean = dateTime.isBefore(other)