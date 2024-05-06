package pj.domain.myDomain

import pj.domain.DomainError.DuplicateError
import pj.domain.{DomainError, Result}
import pj.opaqueTypes.OPreference.OPreference
import pj.opaqueTypes.OTime.OTime
import pj.xml.XML

import java.time.LocalDateTime
import scala.xml.Node


final case class OAvailability private(
                                        OPeriod: OPeriod,
                                        preference: OPreference
                                      )

object OAvailability:
  def from(OPeriod: OPeriod, preference: OPreference): OAvailability =
    new OAvailability(OPeriod, preference)

  def from(start: OTime, end: OTime, preference: OPreference): Result[OAvailability] =
    for
      operiod <- OPeriod.from(start, end)
    yield OAvailability(operiod, preference)

