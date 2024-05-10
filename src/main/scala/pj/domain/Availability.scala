package pj.domain

import pj.domain.DomainError.DuplicateError
import pj.domain.{DomainError, Result}
import pj.opaqueTypes.Preference.Preference
import pj.opaqueTypes.OTime.OTime
import pj.xml.XML

import java.time.LocalDateTime
import scala.xml.Node


final case class Availability private(
                                        period: Period,
                                        preference: Preference
                                      )

object Availability:
  def from(period: Period, preference: Preference): Availability =
    new Availability(period, preference)

  def from(start: OTime, end: OTime, preference: Preference): Result[Availability] =
    for
      operiod <- Period.from(start, end)
    yield Availability(operiod, preference)

