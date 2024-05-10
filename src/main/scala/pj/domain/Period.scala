package pj.domain

import pj.domain.{DomainError, Result}
import pj.opaqueTypes.OTime.OTime

import java.time.LocalDateTime
import scala.annotation.targetName
import scala.util.{Failure, Success, Try}


final case class Period private(start: OTime, end: OTime):

  def precedes(intervalB: Period): Boolean =
    this.end.isBefore(intervalB.start)

  def meets(interval: Period): Boolean =
    this.end.isEqual(interval.start)

  def overlaps(intervalB: Period): Boolean =
    this.start.isBefore(intervalB.start)
    &&
    this.end.isAfter(intervalB.start)
    &&
    this.end.isBefore(intervalB.end)

  def finishedBy(intervalB: Period): Boolean =
    this.start.isBefore(intervalB.start)
    &&
    this.end.isEqual(intervalB.end)

  def contains(intervalB: Period): Boolean =
    this.start.isBefore(intervalB.start)
    &&
    this.end.isAfter(intervalB.end)

  def starts(intervalB: Period): Boolean =
    this.start.isEqual(intervalB.start)
    &&
    this.end.isBefore(intervalB.end)

  def equals(intervalB: Period): Boolean =
    this.start.isEqual(intervalB.start)
    &&
    this.end.isEqual(intervalB.end)

  def startedBy(intervalB: Period): Boolean =
    this.start.isEqual(intervalB.start)
    &&
    this.end.isAfter(intervalB.end)

  def finishes(intervalB: Period): Boolean =
    this.start.isAfter(intervalB.start) && this.end.isEqual(intervalB.end)

  def isPartOf(intervalB: Period): Boolean =
    intervalB.contains(this) || this.starts(intervalB) || this.equals(intervalB) || this.finishes(intervalB)


object Period:
  def from(start: OTime, end: OTime): Result[Period] =
    for {
      _ <- validatePeriod(start, end)
    } yield Period(start, end)

  private def validatePeriod(start: OTime, end: OTime): Result[Unit] =
    if (start.isBefore(end)) Right(())
    else Left(DomainError.InvalidInterval(s"$start - $end"))

