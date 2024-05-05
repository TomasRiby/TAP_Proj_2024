package pj.domain.myDomain

import pj.domain.{DomainError, Result}
import pj.opaqueTypes.OTime.OTime

import java.time.LocalDateTime
import scala.annotation.targetName
import scala.util.{Failure, Success, Try}


final case class OPeriod private(start: OTime, end: OTime):

  def precedes(intervalB: OPeriod): Boolean =
    this.end.isBefore(intervalB.start)

  def meets(interval: OPeriod): Boolean =
    this.end.isEqual(interval.start)

  def overlaps(intervalB: OPeriod): Boolean =
    this.start.isBefore(intervalB.start)
    &&
    this.end.isAfter(intervalB.start)
    &&
    this.end.isBefore(intervalB.end)

  def finishedBy(intervalB: OPeriod): Boolean =
    this.start.isBefore(intervalB.start)
    &&
    this.end.isEqual(intervalB.end)

  def contains(intervalB: OPeriod): Boolean =
    this.start.isBefore(intervalB.start)
    &&
    this.end.isAfter(intervalB.end)

  def starts(intervalB: OPeriod): Boolean =
    this.start.isEqual(intervalB.start)
    &&
    this.end.isBefore(intervalB.end)

  def equals(intervalB: OPeriod): Boolean =
    this.start.isEqual(intervalB.start)
    &&
    this.end.isEqual(intervalB.end)

  def startedBy(intervalB: OPeriod): Boolean =
    this.start.isEqual(intervalB.start)
    &&
    this.end.isAfter(intervalB.end)

  def finishes(intervalB: OPeriod): Boolean =
    this.start.isAfter(intervalB.start) && this.end.isEqual(intervalB.end)

  def isPartOf(intervalB: OPeriod): Boolean =
    intervalB.contains(this) || this.starts(intervalB) || this.equals(intervalB) || this.finishes(intervalB)


object OPeriod:
  def from(start: OTime, end: OTime): Result[OPeriod] =
    for {
      _ <- validateInterval(start, end)
    } yield OPeriod(start, end)

  private def validateInterval(start: OTime, end: OTime): Result[Unit] =
    if (start.isBefore(end)) Right(())
    else Left(DomainError.InvalidInterval(s"$start - $end"))

