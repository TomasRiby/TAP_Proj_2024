package pj.io

import pj.domain.*
import pj.domain.{Availability, Period}
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.OTime
import pj.opaqueTypes.OTime.OTime

object AvailabilityIO:

  def updateAvailabilities(timeSlot: Period, availabilities: List[Availability]): Result[List[Availability]] =
    val result = for {
      intersected <- intersectedAvailability(timeSlot, availabilities)
      filtered <- filterAvailability(intersected, availabilities)
      newAvailabilities <- splitAvailability(intersected, timeSlot)
    } yield filtered ++ newAvailabilities

    result.left.map(_ => DomainError.ImpossibleSchedule)

  def splitAvailability(availability: Availability, timeSlot: Period): Result[List[Availability]] =
    if (availability.period.equals(timeSlot)) Right(Nil)
    else if (availability.period.startedBy(timeSlot)) createRightIntersecting(availability, timeSlot)
    else if (availability.period.finishedBy(timeSlot)) createLeftIntersecting(availability, timeSlot)
    else if (availability.period.contains(timeSlot)) createEncompassing(availability, timeSlot)
    else Left(DomainError.AVAILABILITY_SPLIT_ERROR(s"splitAvailability: $availability - $timeSlot"))

  private def createLeftIntersecting(availability: Availability, timeSlot: Period): Result[List[Availability]] =
    Availability.from(availability.period.start, timeSlot.start, availability.preference).map(List(_))

  private def createRightIntersecting(availability: Availability, timeSlot: Period): Result[List[Availability]] =
    Availability.from(timeSlot.end, availability.period.end, availability.preference).map(List(_))

  private def createEncompassing(availability: Availability, timeSlot: Period): Result[List[Availability]] =
    for {
      leftPart <- Availability.from(availability.period.start, timeSlot.start, availability.preference)
      rightPart <- Availability.from(timeSlot.end, availability.period.end, availability.preference)
    } yield List(leftPart, rightPart)

  private def intersectedAvailability(timeSlot: Period, availabilities: List[Availability]): Result[Availability] =
    availabilities.find(availability => timeSlot.isPartOf(availability.period))
      .toRight(DomainError.AVAILABILITY_SPLIT_ERROR(s"intersectedAvailability: $timeSlot"))

  private def filterAvailability(availability: Availability, availabilities: List[Availability]): Result[List[Availability]] =
    Right(availabilities.filterNot(_ == availability))

  def findEarliestCommonAvailability(resources: List[Teacher | External], duration: ODuration): Result[Period] =
    val commonAvailabilities = resources.flatMap {
      case teacher: Teacher => teacher.availability
      case external: External => external.availability
    }.filter { availability =>
      resources.forall:
        case teacher: Teacher => isAvailableForDuration(teacher.availability, availability, duration)
        case external: External => isAvailableForDuration(external.availability, availability, duration)
    }

    commonAvailabilities.sortBy(_.period.start) match
      case Nil => Left(DomainError.ImpossibleSchedule)
      case head :: _ => createPeriodWithDuration(head.period.start, duration)

  private def isAvailableForDuration(availabilities: List[Availability], availability: Availability, duration: ODuration): Boolean =
    availabilities.exists { avail =>
      val adjustedEnd = availability.period.start.toLocalDateT.plus(duration.toDuration)
      val availStart = availability.period.start
      val periodStart = avail.period.start
      val periodEnd = avail.period.end.toLocalDateT
      (availStart.isAfter(periodStart) || availStart.isEqual(periodStart)) &&
        (adjustedEnd.isBefore(periodEnd) || adjustedEnd.isEqual(periodEnd))
    }

  private def createPeriodWithDuration(start: OTime, duration: ODuration): Result[Period] =
    for {
      end <- OTime.createTime(start.toLocalDateT.plus(duration.toDuration))
      interval <- Period.from(start, end)
    } yield interval