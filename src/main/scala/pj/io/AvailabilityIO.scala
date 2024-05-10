package pj.io

import pj.domain.*
import pj.domain.{Availability, Period}
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.OTime

object AvailabilityIO:

  def updateAvailabilities(timeSlot: Period, availabilities: List[Availability]): Result[List[Availability]] =
    val result = for {
      intersected <- intersectedOAvailability(timeSlot, availabilities)
      filtered <- filterAvail(intersected, availabilities)
      newAvail <- splitAvail(intersected, timeSlot)
    } yield filtered ++ newAvail
    //    println(s"UpdatedAvail $result")
    result match
      case Left(error) => Left(DomainError.ImpossibleSchedule)
      case Right(value) => Right(value)

  def splitAvail(availability: Availability, timeSlot: Period): Result[List[Availability]] =
    if (availability.period.equals(timeSlot)) Right(List.empty[Availability])
    else if (availability.period.startedBy(timeSlot)) createIntersectingRight(availability, timeSlot)
    else if (availability.period.finishedBy(timeSlot)) createIntersectingLeft(availability, timeSlot)
    else if (availability.period.contains(timeSlot)) createEncompassing(availability, timeSlot)
    else Left(DomainError.Huh(s"SplitAvail $availability - $timeSlot"))

  private def createIntersectingLeft(availability: Availability, timeSlot: Period): Result[List[Availability]] =
    for {
      avail <- Availability.from(availability.period.start, timeSlot.start, availability.preference)
    } yield List(avail)

  private def createIntersectingRight(availability: Availability, timeSlot: Period): Result[List[Availability]] =
    for {
      avail <- Availability.from(timeSlot.end, availability.period.end, availability.preference)
    } yield List(avail)

  private def createEncompassing(availability: Availability, timeSlot: Period): Result[List[Availability]] =
    for
      availability1 <- Availability.from(availability.period.start, timeSlot.start, availability.preference)
      availability2 <- Availability.from(timeSlot.end, availability.period.end, availability.preference)
    yield List(availability1, availability2)

  private def intersectedOAvailability(timeSlot: Period, availabilities: List[Availability]): Result[Availability] =
    val intersectedOAvailability = availabilities.find(availability => timeSlot.isPartOf(availability.period))
    intersectedOAvailability match
      case None => Left(DomainError.Huh(s"intersectedOAvailability $intersectedOAvailability - $timeSlot"))
      case Some(avail) => Right(avail)

  private def filterAvail(availability: Availability, availabilities: List[Availability]): Result[List[Availability]] =
    Right(availabilities.filter(_ != availability))

  def findEarliestCommonOAvailability(resources: List[Teacher | External], duration: ODuration): Result[Period] =

    val commonAvailabilities = resources.flatMap {
      case teacher: Teacher => teacher.availability
      case external: External => external.availability
    }.filter { availability =>
      resources.forall:
        case teacher: Teacher =>
          teacher.availability.exists { a2 =>
            val adjustedEnd = availability.period.start.toLocalDateT.plus(duration.toDuration)
            val availStart = availability.period.start
            val periodStart = a2.period.start
            val periodEnd = a2.period.end.toLocalDateT
            (availStart.isAfter(periodStart) || availStart.isEqual(periodStart)) &&
              (adjustedEnd.isBefore(periodEnd) || adjustedEnd.isEqual(periodEnd))
          }
        case external: External =>
          external.availability.exists { a2 =>
            val adjustedEnd = availability.period.start.toLocalDateT.plus(duration.toDuration)
            val availStart = availability.period.start
            val periodStart = a2.period.start
            val periodEnd = a2.period.end.toLocalDateT
            (availStart.isAfter(periodStart) || availStart.isEqual(periodStart)) &&
              (adjustedEnd.isBefore(periodEnd) || adjustedEnd.isEqual(periodEnd))
          }
    }

    val sortedAvailabilities = commonAvailabilities.sortBy(_.period.start)
    sortedAvailabilities match
      case Nil => Left(DomainError.ImpossibleSchedule)
      case head :: _ =>
        for {
          end <- OTime.createTime(head.period.start.toLocalDateT.plus(duration.toDuration))
          interval <- Period.from(head.period.start, end)
        } yield interval