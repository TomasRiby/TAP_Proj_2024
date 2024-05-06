package pj.io

import pj.domain.*
import pj.domain.myDomain.{OAvailability, OPeriod}
import pj.opaqueTypes.OTime

object OAvailabilityIO:

  def updateAvailabilities(timeSlot: OPeriod, availabilities: List[OAvailability]): Result[List[OAvailability]] =
    val result = for {
      intersected <- intersectedOAvailability(timeSlot, availabilities)
      filtered <- filterAvail(intersected, availabilities)
      newAvail <- splitAvail(intersected, timeSlot)
    } yield filtered ++ newAvail
    //    println(s"UpdatedAvail $result")
    result match
      case Left(error) => Left(DomainError.ImpossibleSchedule)
      case Right(value) => Right(value)

  def splitAvail(availability: OAvailability, timeSlot: OPeriod): Result[List[OAvailability]] =
    if (availability.OPeriod.equals(timeSlot)) Right(List.empty[OAvailability])
    else if (availability.OPeriod.startedBy(timeSlot)) createIntersectingRight(availability, timeSlot)
    else if (availability.OPeriod.finishedBy(timeSlot)) createIntersectingLeft(availability, timeSlot)
    else if (availability.OPeriod.contains(timeSlot)) createEncompassing(availability, timeSlot)
    else Left(DomainError.Huh(s"SplitAvail $availability - $timeSlot"))

  private def createIntersectingLeft(availability: OAvailability, timeSlot: OPeriod): Result[List[OAvailability]] =
    for {
      avail <- OAvailability.from(availability.OPeriod.start, timeSlot.start, availability.preference)
    } yield List(avail)

  private def createIntersectingRight(availability: OAvailability, timeSlot: OPeriod): Result[List[OAvailability]] =
    for {
      avail <- OAvailability.from(timeSlot.end, availability.OPeriod.end, availability.preference)
    } yield List(avail)

  private def createEncompassing(availability: OAvailability, timeSlot: OPeriod): Result[List[OAvailability]] =
    for
      availability1 <- OAvailability.from(availability.OPeriod.start, timeSlot.start, availability.preference)
      availability2 <- OAvailability.from(timeSlot.end, availability.OPeriod.end, availability.preference)
    yield List(availability1, availability2)

  private def intersectedOAvailability(timeSlot: OPeriod, availabilities: List[OAvailability]): Result[OAvailability] =
    val intersectedOAvailability = availabilities.find(availability => timeSlot.isPartOf(availability.OPeriod))
    intersectedOAvailability match
      case None => Left(DomainError.Huh(s"intersectedOAvailability $intersectedOAvailability - $timeSlot"))
      case Some(avail) => Right(avail)

  private def filterAvail(availability: OAvailability, availabilities: List[OAvailability]): Result[List[OAvailability]] =
    Right(availabilities.filter(_ != availability))

  def findEarliestCommonOAvailability(resources: List[OTeacher | OExternal], duration: Time): Result[OPeriod] =

    val commonAvailabilities = resources.flatMap {
      case teacher: OTeacher => teacher.availability
      case external: OExternal => external.availability
    }.filter { availability =>
      resources.forall {
        case teacher: OTeacher =>
          teacher.availability.exists { a2 =>
            val adjustedEnd = availability.OPeriod.start.toLocalDateT
              .plusHours(duration.hour.to)
              .plusMinutes(duration.minutes.to)
              .plusSeconds(duration.seconds.to)
            (availability.OPeriod.start.isAfter(a2.OPeriod.start) || availability.OPeriod.start.isEqual(a2.OPeriod.start)) &&
              (adjustedEnd.isBefore(a2.OPeriod.end.toLocalDateT) || adjustedEnd.isEqual(a2.OPeriod.end.toLocalDateT))
          }
        case external: OExternal =>
          external.availability.exists { a2 =>
            val adjustedEnd = availability.OPeriod.start.toLocalDateT
              .plusHours(duration.hour.to)
              .plusMinutes(duration.minutes.to)
              .plusSeconds(duration.seconds.to)
            (availability.OPeriod.start.isAfter(a2.OPeriod.start) || availability.OPeriod.start.isEqual(a2.OPeriod.start)) &&
              (adjustedEnd.isBefore(a2.OPeriod.end.toLocalDateT) || adjustedEnd.isEqual(a2.OPeriod.end.toLocalDateT))
          }
      }
    }

    val sortedAvailabilities = commonAvailabilities.sortBy(_.OPeriod.start)
    sortedAvailabilities match
      case Nil => Left(DomainError.ImpossibleSchedule)
      case head :: _ =>
        for {
          end <- OTime.createTime(head.OPeriod.start.toLocalDateT.plusHours(duration.hour.to).plusMinutes(duration.minutes.to).plusSeconds(duration.seconds.to))
          interval <- OPeriod.from(head.OPeriod.start, end)
        } yield interval