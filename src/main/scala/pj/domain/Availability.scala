package pj.domain

import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.{OTime, Preference}
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference.Preference
import pj.xml.XML

import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec
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

  def findAllPossibleAvailabilitiesSlot(availabilities: List[List[Availability]], duration: ODuration): List[Availability] =
    @tailrec
    def combine(availabilities: List[List[Availability]], acc: List[Availability]): List[Availability] =
      availabilities match
        case Nil => acc
        case head :: tail =>
          val combined = for {
            a <- head
            b <- acc
            start = if (a.start.isAfter(b.start)) a.start else b.start
            end = if (a.end.isBefore(b.end)) a.end else b.end
            if start.isBefore(end) && Duration.between(start.toTemporal, end.toTemporal).compareTo(duration.toDuration) >= 0
          } yield Availability.from(start, end, Preference.fromMoreThan5(a.preference + b.preference))
          combine(tail, combined)

    // Initialize the combination process with the first set of availabilities
    val combinedAvail = combine(availabilities.drop(1), availabilities.headOption.getOrElse(List.empty))
    combinedAvail.sortBy(_.start)

