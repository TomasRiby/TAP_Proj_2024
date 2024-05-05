package pj.domain.myDomain

import pj.domain.DomainError.DuplicateError
import pj.domain.{DomainError, Result}
import pj.opaqueTypes.OPreference.OPreference
import pj.opaqueTypes.OTime.OTime
import pj.xml.XML

import java.time.LocalDateTime
import scala.xml.Node


final case class OAvailability(
                                OPeriod: OPeriod,
                                preference: OPreference
                             )

object OAvailability:
  def from(OPeriod: OPeriod, preference: OPreference): OAvailability =
    OAvailability(OPeriod, preference)

