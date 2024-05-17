package pj.domain


import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.{OTime, Preference}
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference.Preference

import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec


object AlgorithmV2:
  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
  def makeTheAlgorithmHappen(agenda: Agenda): Result[ScheduleOut] =
    val teacherList = agenda.resources.teacher
    val externalList = agenda.resources.external
    val duration = agenda.duration

    val preVivaList = agenda.vivas.map(PreViva.linkVivaWithResource(_, teacherList, externalList))


    def preVivaToMap(vivaList: Seq[PreViva]): Map[Set[President | Advisor | Supervisor | CoAdvisor], List[List[Availability]]] =
      val roles = vivaList.map(viva => viva.roleLinkedWithResourceList.map(_.role).toSet)
      val availabilities = vivaList.map(viva => viva.roleLinkedWithResourceList.map(_.listAvailability))
      roles.zip(availabilities).toMap

    val availabilityMap = preVivaToMap(preVivaList)


    // Schedule each viva
    val (scheduledVivas, _: List[Availability]) = preVivaList.foldLeft((List.empty[PosViva], List.empty[Availability])) { case ((acc, usedSlots), viva) =>
      val roleSet = viva.roleLinkedWithResourceList.map(_.role).toSet
      val availabilities = availabilityMap(roleSet)
      val possibleSlots = Availability.findAllPossibleAvailabilitiesSlot(availabilities, agenda.duration)

      val updatedPossibleSlots = Availability.updateAvailabilitySlots(possibleSlots, agenda.duration, usedSlots)

      val (chosenSlotOpt, updatedUsedSlots) = Availability.chooseFirstPossibleAvailabilitiesSlot(updatedPossibleSlots, agenda.duration, usedSlots)

      chosenSlotOpt match
        case Some((start, end, preference)) =>
          val scheduledViva = PosViva(
            viva.student.toString,
            viva.title.toString,
            start.format(formatter),
            end.format(formatter),
            preference,
            (viva.roleLinkedWithResourceList.collectFirst { case RoleLinkedWithResource(p: President, name, _) => name }getOrElse()).toString,
            (viva.roleLinkedWithResourceList.collectFirst { case RoleLinkedWithResource(p: Advisor, name, _) => name }getOrElse()).toString,
            viva.roleLinkedWithResourceList.collect { case RoleLinkedWithResource(s: Supervisor, name, _) => name.toString },
             viva.roleLinkedWithResourceList.collect { case RoleLinkedWithResource(c: CoAdvisor, name, _) => name.toString },
          )
          (scheduledViva :: acc, updatedUsedSlots)
        case None => (acc, usedSlots) // If no slot found, ignore the viva
    }
    val sortedScheduleVivas = scheduledVivas.sortBy(a => LocalDateTime.parse(a.start))

    val scheduleOut = ScheduleOut.from(sortedScheduleVivas)
    Right(scheduleOut)



