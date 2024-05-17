package pj.domain

import pj.opaqueTypes.{OTime, Preference}
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference.Preference
import pj.xml.XML

import java.time.LocalDateTime
import scala.xml.Node


final case class Availability(
                               start: OTime,
                               end: OTime,
                               preference: Preference
                             )

object Availability:
  def from(start: OTime, end: OTime, preference: Preference) =
    new Availability(start: OTime, end: OTime, preference: Preference)

  def createLocal(start: LocalDateTime, end: LocalDateTime, preference: Int): Availability =
    val res = for {
      start <- OTime.createTime(start)
      end <- OTime.createTime(end)
      resPreference = Preference.fromMoreThan5(preference)
    } yield Availability.from(start, end, resPreference)
    res match
      case Right(value) => value
    

  def intersects(availability1: Availability, availability2: Availability): Boolean =
    (availability1.start.isBefore(availability2.end) && availability1.end.isAfter(availability2.start)) ||
      (availability2.start.isBefore(availability1.end) && availability2.end.isAfter(availability1.start))

