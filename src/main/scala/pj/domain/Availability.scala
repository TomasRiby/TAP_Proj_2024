package pj.domain

import pj.typeUtils.opaqueTypes.opaqueTypes.{Time, Preference}
import pj.xml.XML
import java.time.LocalDateTime


import scala.xml.Node


final case class Availability(
                               start: Time,
                               end: Time,
                               preference: Preference
                             )

object Availability:
  def from(start: Time, end: Time, preference: Preference) =
    new Availability(start: Time, end: Time, preference: Preference)
