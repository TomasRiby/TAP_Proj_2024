package pj.io

import pj.domain.*

object AvailabilityIO:

  def updateAvailabilities(timeSlot: Interval, availabilities: List[Availability]): Result[List[Availability]] =
    val result = for {
      intersected <- intersectedAvailability(timeSlot, availabilities)
      filtered <- filterAvail(intersected, availabilities)
      newAvail <- splitAvail(intersected, timeSlot)
    } yield filtered ++ newAvail
//    println(s"UpdatedAvail $result")
    result match
      case Left(error) => Left(DomainError.ImpossibleSchedule)
      case Right(value) => Right(value)

  def splitAvail(availability: Availability, timeSlot: Interval): Result[List[Availability]] =
    if (availability.interval.equals(timeSlot)) Right(List.empty[Availability])
    else if (availability.interval.startedBy(timeSlot)) createIntersectingRight(availability, timeSlot)
    else if (availability.interval.finishedBy(timeSlot)) createIntersectingLeft(availability, timeSlot)
    else if (availability.interval.contains(timeSlot)) createEncompassing(availability, timeSlot)
    else Left(DomainError.Huh(s"SplitAvail $availability - $timeSlot"))

  private def createIntersectingLeft(availability: Availability, timeSlot: Interval): Result[List[Availability]] =
    for {
      avail <- Availability.from(availability.interval.start, timeSlot.start, availability.preference)
    } yield List(avail)

  private def createIntersectingRight(availability: Availability, timeSlot: Interval): Result[List[Availability]] =
    for {
      avail <- Availability.from(timeSlot.end, availability.interval.end, availability.preference)
    } yield List(avail)

  private def createEncompassing(availability: Availability, timeSlot: Interval): Result[List[Availability]] =
    for
      availability1 <- Availability.from(availability.interval.start, timeSlot.start, availability.preference)
      availability2 <- Availability.from(timeSlot.end, availability.interval.end, availability.preference)
    yield List(availability1, availability2)

  private def intersectedAvailability(timeSlot: Interval, availabilities: List[Availability]): Result[Availability] =
    val intersectedAvailability = availabilities.find(availability => timeSlot.isPartOf(availability.interval))
    intersectedAvailability match
      case None => Left(DomainError.Huh(s"intersectedAvailability $intersectedAvailability - $timeSlot"))
      case Some(avail) => Right(avail)

  private def filterAvail(availability: Availability, availabilities: List[Availability]): Result[List[Availability]] =
    Right(availabilities.filter(_ != availability))

  def findEarliestCommonAvailability(resources: List[Resource], duration: Time): Result[Interval] =
    val commonAvailabilities = resources.flatMap(_.availabilities).filter { availability =>
      resources.forall { r2 =>
        r2.availabilities.exists { a2 =>
          val adjustedEnd = availability.interval.start.to
            .plusHours(duration.hour.to)
            .plusMinutes(duration.minutes.to)
            .plusSeconds(duration.seconds.to)
          (availability.interval.start.isAfter(a2.interval.start) || availability.interval.start.isEqual(a2.interval.start)) &&
            (adjustedEnd.isBefore(a2.interval.end.to) || adjustedEnd.isEqual(a2.interval.end.to))
        }
      }
    }
    val sortedAvailabilities = commonAvailabilities.sortBy(_.interval.start.to)
    sortedAvailabilities match
      case Nil => Left(DomainError.ImpossibleSchedule)
      case head :: _ =>
        for {
          end <- DateTime.from(head.interval.start.to.plusHours(duration.hour.to).plusMinutes(duration.minutes.to).plusSeconds(duration.seconds.to))
          interval <- Interval.from(head.interval.start, end)
        } yield interval