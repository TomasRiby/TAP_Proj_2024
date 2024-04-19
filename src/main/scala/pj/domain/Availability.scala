package pj.domain

import pj.domain.DomainError.DuplicateError
import pj.typeUtils.opaqueTypes.opaqueTypes.{Preference, Time}
import pj.xml.XML

import java.time.LocalDateTime
import scala.xml.Node


final case class Availability(
                               start: Time,
                               end: Time,
                               preference: Preference
                             )

object Availability:
  def from(start: Time, end: Time, preference: Preference): Result[Availability] =
    if (start.isBefore(end)) // Check if start is before or at the same time as end
      Right(new Availability(start, end, preference))

    else
      Left(DomainError.WrongFormat(s"Start $start has to be before the End $end"))


  def intersects(availability1: Availability, availability2: Availability): Boolean =
    (availability1.start.isBefore(availability2.end) && availability1.end.isAfter(availability2.start)) ||
      (availability2.start.isBefore(availability1.end) && availability2.end.isAfter(availability1.start))

