package pj.domain

import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.OTime
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference.Preference

import java.time.{Duration, LocalDateTime}
import java.time.format.DateTimeFormatter

object NewAlgo:

  def greedyScheduleAlgorithm(agenda: Agenda): Either[DomainError, ScheduleOut] =
    val vivas = agenda.vivas
    val resources = agenda.resources
    val duration = agenda.duration.toDuration

    case class ResourceAvailability(resourceId: ID, availabilities: List[Availability])

    // Helper function to get all resource availabilities
    def getResourceAvailabilities(resourceId: ID, availabilities: Map[ID, List[Availability]]): List[Availability] =
      availabilities.getOrElse(resourceId, List())

    // Function to check if two time intervals overlap
    def overlaps(aStart: LocalDateTime, aEnd: LocalDateTime, bStart: LocalDateTime, bEnd: LocalDateTime): Boolean =
      aStart.isBefore(bEnd) && bStart.isBefore(aEnd)

    // Function to generate smaller time slots of the required duration
    def generateTimeSlots(availability: Availability, duration: Duration): List[(LocalDateTime, LocalDateTime)] =
      @scala.annotation.tailrec
      def loop(start: LocalDateTime, end: LocalDateTime, acc: List[(LocalDateTime, LocalDateTime)]): List[(LocalDateTime, LocalDateTime)] =
        if (start.plus(duration).isAfter(end)) acc
        else loop(start.plus(duration), end, (start, start.plus(duration)) :: acc)
      loop(availability.start.toLocalDateTime, availability.end.toLocalDateTime, List.empty).reverse

    // Find all possible slots for a viva
    def findPossibleSlots(viva: Viva, availabilities: Map[ID, List[Availability]]): List[(LocalDateTime, LocalDateTime)] =
      val requiredResourceIds = List(viva.president.id, viva.advisor.id) ++ viva.supervisor.map(_.id) ++ viva.coAdvisor.map(_.id)
      val availableSlots = requiredResourceIds.flatMap(getResourceAvailabilities(_, availabilities))
      availableSlots.flatMap(generateTimeSlots(_, duration))

    // Function to check if a slot is available for all required resources
    def areAllResourcesAvailable(slot: (LocalDateTime, LocalDateTime), viva: Viva, scheduledVivas: List[(Viva, (LocalDateTime, LocalDateTime))], availabilities: Map[ID, List[Availability]]): Boolean =
      val (slotStart, slotEnd) = slot
      val requiredResourceIds = List(viva.president.id, viva.advisor.id) ++ viva.supervisor.map(_.id)
      requiredResourceIds.forall { id =>
        getResourceAvailabilities(id, availabilities).exists { a =>
          (a.start.toLocalDateTime.isBefore(slotStart) || a.start.toLocalDateTime.equals(slotStart)) &&
            (a.end.toLocalDateTime.isAfter(slotEnd) || a.end.toLocalDateTime.equals(slotEnd)) &&
            !scheduledVivas.exists { case (_, (start, end)) =>
              overlaps(slotStart, slotEnd, start, end)
            }
        }
      }

    // Convert LocalDateTime to OTime
    def toOTime(localDateTime: LocalDateTime): OTime =
      OTime.from(localDateTime)

    // Function to update resource availabilities after scheduling a viva
    def updateAvailabilities(slot: (LocalDateTime, LocalDateTime), viva: Viva, availabilities: Map[ID, List[Availability]]): Map[ID, List[Availability]] =
      val (slotStart, slotEnd) = slot
      val slotStartOTime = toOTime(slotStart)
      val slotEndOTime = toOTime(slotEnd)
      val requiredResourceIds = List(viva.president.id, viva.advisor.id) ++ viva.supervisor.map(_.id)
      requiredResourceIds.foldLeft(availabilities) { case (acc, id) =>
        val updatedAvailabilities = getResourceAvailabilities(id, acc).flatMap { a =>
          if (a.start.isBefore(slotStartOTime) && a.end.isAfter(slotEndOTime))
            List(
              Availability(a.start, slotStartOTime, a.preference),
              Availability(slotEndOTime, a.end, a.preference)
            )
          else if (a.start.isBefore(slotStartOTime) && a.end.isAfter(slotStartOTime))
            List(Availability(a.start, slotStartOTime, a.preference))
          else if (a.start.isBefore(slotEndOTime) && a.end.isAfter(slotEndOTime))
            List(Availability(slotEndOTime, a.end, a.preference))
          else
            List(a)
        }
        acc + (id -> updatedAvailabilities)
      }

    // Calculate the total preference for an availability slot
    def calculatePreference(viva: Viva, slot: (LocalDateTime, LocalDateTime), availabilities: Map[ID, List[Availability]]): Int =
      val requiredResourceIds = List(viva.president.id, viva.advisor.id) ++ viva.supervisor.map(_.id)
      requiredResourceIds.flatMap(getResourceAvailabilities(_, availabilities)).filter(a =>
        !slot._1.isAfter(a.end.toLocalDateTime) && !slot._2.isBefore(a.start.toLocalDateTime)
      ).map(_.preference.toInteger).sum

    // Function to get the total preference of a schedule
    def calculateTotalPreference(schedule: List[(Viva, (LocalDateTime, LocalDateTime))], availabilities: Map[ID, List[Availability]]): Int =
      schedule.map { case (viva, slot) =>
        calculatePreference(viva, slot, availabilities)
      }.sum

    // Recursive function to generate all possible schedules and select the best one
    def generateSchedules(vivas: List[Viva], scheduledVivas: List[(Viva, (LocalDateTime, LocalDateTime))], availabilities: Map[ID, List[Availability]]): List[(Viva, (LocalDateTime, LocalDateTime))] =
      vivas match
        case viva :: rest =>
          val allPossibleSlots = findPossibleSlots(viva, availabilities)
          val possibleSchedules = allPossibleSlots.flatMap { slot =>
            if (areAllResourcesAvailable(slot, viva, scheduledVivas, availabilities))
              val updatedAvailabilities = updateAvailabilities(slot, viva, availabilities)
              val newScheduledVivas = (viva -> slot) :: scheduledVivas
              Some(generateSchedules(rest, newScheduledVivas, updatedAvailabilities))
            else
              None
          }
          possibleSchedules.foldLeft(scheduledVivas) { (bestSchedule, currentSchedule) =>
            if (calculateTotalPreference(currentSchedule, availabilities) > calculateTotalPreference(bestSchedule, availabilities))
              currentSchedule
            else
              bestSchedule
          }
        case Nil => scheduledVivas

    // Initialize availabilities
    val initialAvailabilities: Map[ID, List[Availability]] = (resources.teacher.map(t => t.id -> t.availability) ++
      resources.external.map(e => e.id -> e.availability)).toMap

    // Generate the best schedule
    val bestSchedule = generateSchedules(vivas.toList, List.empty, initialAvailabilities).reverse

    // Convert the best schedule to PosViva format
    val scheduledVivas = bestSchedule.map { case (viva, (start, end)) =>
      val preference = calculatePreference(viva, (start, end), initialAvailabilities)
      val presidentName = resources.teacher.find(_.id == viva.president.id).map(_.name.toString).getOrElse("")
      val advisorName = resources.teacher.find(_.id == viva.advisor.id).map(_.name.toString).getOrElse("")
      val coAdvisorNames = viva.coAdvisor.flatMap(id => resources.teacher.find(_.id == id.id).map(_.name.toString))
      val supervisorNames = viva.supervisor.flatMap(id => resources.external.find(_.id == id.id).map(_.name.toString))
      PosViva(
        viva.student.toString,
        viva.title.toString,
        start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        preference,
        presidentName,
        advisorName,
        supervisorNames,
        coAdvisorNames)
    }

    val totalPreference = scheduledVivas.map(_.preference).sum
    val scheduleOut = ScheduleOut.from(scheduledVivas)
    Right(scheduleOut)
