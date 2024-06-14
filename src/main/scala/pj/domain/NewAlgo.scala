package pj.domain

import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.ODuration.ODuration

import java.time.{Duration, LocalDateTime}
import java.time.format.DateTimeFormatter

object NewAlgo:

  def greedyScheduleAlgorithm(agenda: Agenda): Either[DomainError, ScheduleOut] =
    val vivas = agenda.vivas
    val resources = agenda.resources
    val duration = agenda.duration.toDuration

    // Helper function to get all resource availabilities
    def getResourceAvailabilities(resourceIds: List[ID]): List[Availability] =
      val allTeachers = resources.teacher.filter(t => resourceIds.contains(t.id)).flatMap(_.availability)
      val allExternals = resources.external.filter(e => resourceIds.contains(e.id)).flatMap(_.availability)
      allTeachers ++ allExternals

    // Find all possible slots for a viva
    def findPossibleSlots(viva: Viva): List[(Viva, Availability)] =
      val requiredResourceIds: List[ID] = List(Some(viva.president.id), Some(viva.advisor.id)).flatten ++ viva.coAdvisor.map(_.id) ++ viva.supervisor.map(_.id)
      val availabilities = getResourceAvailabilities(requiredResourceIds)
      availabilities.filter(a => Duration.between(a.start.toTemporal, a.end.toTemporal).compareTo(duration) >= 0).map(a => (viva, a))

    // Function to check if a slot is available
    def isSlotAvailable(scheduledVivas: List[PosViva], newSlot: (Viva, Availability)): Boolean =
      val (_, availability) = newSlot
      !scheduledVivas.exists { sv =>
        LocalDateTime.parse(sv.start).isBefore(availability.end.toLocalDateTime) && LocalDateTime.parse(sv.end).isAfter(availability.start.toLocalDateTime)
      }

    // Calculate the total preference for a viva
    def calculatePreference(viva: Viva, availability: Availability): Int =
      val requiredResourceIds: List[ID] = List(Some(viva.president.id), Some(viva.advisor.id)).flatten ++ viva.coAdvisor.map(_.id) ++ viva.supervisor.map(_.id)
      val availabilities = getResourceAvailabilities(requiredResourceIds).filter(a =>
        !availability.start.isAfter(a.end) && !availability.end.isBefore(a.start)
      )
      availabilities.map(_.preference.toInteger).sum

    // Recursive function to schedule vivas
    def scheduleVivas(vivas: Seq[Viva], scheduledVivas: List[PosViva]): List[PosViva] =
      vivas.headOption match
        case Some(viva) =>
          val allPossibleSlots = findPossibleSlots(viva).sortBy(-_._2.preference)
          allPossibleSlots.foldLeft(scheduledVivas) { (acc, slot) =>
            if (isSlotAvailable(acc, slot))
              val (viva, availability) = slot
              val start = availability.start
              val endTest = start.plusSeconds(duration.toSeconds)
              val preference = calculatePreference(viva, availability)
              val presidentName = resources.teacher.find(_.id == viva.president.id).map(_.name.toString).getOrElse("")
              val advisorName = resources.teacher.find(_.id == viva.advisor.id).map(_.name.toString).getOrElse("")
              val coAdvisorNames = viva.coAdvisor.flatMap(id => resources.teacher.find(_.id == id.id).map(_.name.toString))
              val supervisorNames = viva.supervisor.flatMap(id => resources.external.find(_.id == id.id).map(_.name.toString))
              val posViva = PosViva(viva.student.toString, viva.title.toString, start.toLocalDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), endTest.toLocalDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), preference, presidentName, advisorName, supervisorNames, coAdvisorNames)
              scheduleVivas(vivas.drop(1), posViva :: acc)
            else
              acc
          }
        case None => scheduledVivas

    // Start scheduling
    val scheduledVivas = scheduleVivas(vivas, List.empty[PosViva])

    val scheduleOut = ScheduleOut.from(scheduledVivas)
    Right(scheduleOut)
