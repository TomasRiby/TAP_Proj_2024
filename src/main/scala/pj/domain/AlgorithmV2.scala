package pj.domain


import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.{OTime, Preference}
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference.Preference

import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec


object AlgorithmV2:
  def makeTheAlgorithmHappen(agenda: Agenda): Result[Unit] =
    val teacherList = agenda.resources.teacher
    val externalList = agenda.resources.external
    val duration = agenda.duration

    val preVivaList = agenda.vivas.map(PreViva.linkVivaWithResource(_, teacherList, externalList))


    def preVivaToMap(vivaList: Seq[PreViva]): Map[Set[President | Advisor | Supervisor | CoAdvisor], List[List[Availability]]] =
      val roles = vivaList.map(viva => viva.roleLinkedWithResourceList.map(_.role).toSet)
      val availabilities = vivaList.map(viva => viva.roleLinkedWithResourceList.map(_.listAvailability))
      roles.zip(availabilities).toMap

    val availabilityMap = preVivaToMap(preVivaList)

    def chooseFirstPossibleAvailabilitiesSlot(availabilities: List[Availability], duration: ODuration, usedSlots: List[Availability]): (Option[(LocalDateTime, LocalDateTime, Int)], List[Availability]) =
      val availableSlots = availabilities.filterNot(usedSlots.contains)
      availableSlots.headOption match
        case Some(slot) =>
          val start = slot.start.toLocalDateTime
          val end = start.plus(duration.toDuration)
          (Some((start, end, slot.preference.toInteger)), slot :: usedSlots)
        case None =>
          (None, usedSlots)


    // Schedule each viva
    val (scheduledVivas, _: List[Availability]) = preVivaList.foldLeft((List.empty[PosViva], List.empty[Availability])) { case ((acc, usedSlots), viva) =>
      val roleSet = viva.roleLinkedWithResourceList.map(_.role).toSet
      val availabilities = availabilityMap(roleSet)
      val possibleSlots = Availability.findAllPossibleAvailabilitiesSlot(availabilities, agenda.duration)

      val (chosenSlotOpt, updatedUsedSlots) = chooseFirstPossibleAvailabilitiesSlot(possibleSlots, agenda.duration, usedSlots)
      println (chosenSlotOpt)


      //          val scheduledViva = PosViva(
      //            viva.student.toString,
      //            viva.title.toString,
      //            chosenSlot.start.toString,
      //            chosenSlot.end.toString,
      //            chosenSlot.preference.toInteger,
      //            viva.roleLinkedWithResourceList.collectFirst { case RoleLinkedWithResource(p: President, name, _) => name }.getOrElse("Unknown"),
      //            viva.roleLinkedWithResourceList.collectFirst { case RoleLinkedWithResource(a: Advisor, name, _) => name }.getOrElse("Unknown"),
      //            viva.roleLinkedWithResourceList.collect { case RoleLinkedWithResource(s: Supervisor, name, _) => name },
      //            viva.roleLinkedWithResourceList.collect { case RoleLinkedWithResource(c: CoAdvisor, name, _) => name }
      //          )
      //          scheduledViva :: acc
      //        case None => acc // If no slot found, ignore the viva
      (List(), updatedUsedSlots)
    } // Reverse at the end to maintain the original order
    Right(())



