package pj.domain

import java.time.LocalDateTime
import scala.annotation.targetName
import scala.util.{Failure, Success, Try}

opaque type DateTime = LocalDateTime

object DateTime:
  def from(dateTime: String): Result[DateTime] =
    Try(LocalDateTime.parse(dateTime)) match
      case Failure(exception) => Left(DomainError.InvalidDateTime(s"$dateTime - Date should follow the format yyyy-mm-ddThh:mm:ss"))
      case Success(value) => Right(value)

  def from(dateTime: LocalDateTime): Result[DateTime] =
    Right(dateTime)
  
  extension(dateTime: DateTime)
    @targetName("DateTime.to")
    def to: LocalDateTime = dateTime

    @targetName("DateTime.isEqual")
    def isEqual(other: DateTime): Boolean = dateTime.isEqual(other)

    @targetName("DateTime.isAfter")
    def isAfter(other: DateTime): Boolean = dateTime.isAfter(other)

    @targetName("DateTime.isBefore")
    def isBefore(other: DateTime): Boolean = dateTime.isBefore(other)


final case class Interval(start: DateTime, end: DateTime):

  def precedes(intervalB: Interval): Boolean =
    this.end.isBefore(intervalB.start)
    
  def meets(interval: Interval): Boolean =
    this.end.isEqual(interval.start)

  def overlaps(intervalB: Interval): Boolean =
    this.start.isBefore(intervalB.start)
      && this.end.isAfter(intervalB.start)
      && this.end.isBefore(intervalB.end)

  def finishedBy(intervalB: Interval): Boolean =
    this.start.isBefore(intervalB.start)
      && this.end.isEqual(intervalB.end)

  def contains(intervalB: Interval): Boolean =
    this.start.isBefore(intervalB.start)
      && this.end.isAfter(intervalB.end)

  def starts(intervalB: Interval): Boolean =
    this.start.isEqual(intervalB.start)
      && this.end.isBefore(intervalB.end)

  def equals(intervalB: Interval): Boolean =
    this.start.isEqual(intervalB.start)
      && this.end.isEqual(intervalB.end)

  def startedBy(intervalB: Interval): Boolean =
    this.start.isEqual(intervalB.start)
      && this.end.isAfter(intervalB.end)

  def finishes(intervalB: Interval): Boolean =
    this.start.isAfter(intervalB.start) && this.end.isEqual(intervalB.end)

  def isPartOf(intervalB: Interval): Boolean =
    intervalB.contains(this) || this.starts(intervalB) || this.equals(intervalB) || this.finishes(intervalB)


object Interval:
  def from(start: DateTime, end: DateTime): Result[Interval] =
    for {
      _ <- validateInterval(start, end)
    } yield Interval(start,end)

  private def validateInterval(start: DateTime,end: DateTime): Result[Unit] =
    if (start.isBefore(end)) Right(())
    else Left(DomainError.InvalidInterval(s"$start - $end"))