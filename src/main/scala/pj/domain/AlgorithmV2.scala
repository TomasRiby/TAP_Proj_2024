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
      combine(availabilities.drop(1), availabilities.headOption.getOrElse(List.empty))

    // Schedule each viva
    val scheduledVivas = preVivaList.foldLeft(List.empty[PosViva]) { (acc, viva) =>
      val availabilities = availabilityMap(viva.roleLinkedWithResourceList.map(_.role).toSet)
      val res = findAllPossibleAvailabilitiesSlot(availabilities, agenda.duration)
      println(res)
      List()
      //      match
      //        case Some(slot) =>
      //          val scheduledViva = PosViva.from(
      //            viva.student.toString,
      //            viva.title.toString,
      //            slot._1.toString,
      //            slot._2.toString,
      //            2, // Placeholder for preference calculation
      //            viva.roleLinkedWithResourceList.collectFirst { case RoleLinkedWithResource(p: President, name, _) => name }.toString,
      //            viva.roleLinkedWithResourceList.collectFirst { case RoleLinkedWithResource(a: Advisor, name, _) => name }.toString,
      //            viva.roleLinkedWithResourceList.collect { case RoleLinkedWithResource(s: Supervisor, name, _) => name.toString },
      //            viva.roleLinkedWithResourceList.collect { case RoleLinkedWithResource(c: CoAdvisor, name, _) => name.toString }
      //          )
      //          val updatedMap = updateAvailabilities(availabilityMap, slot)
      //          scheduledViva :: acc
      //        case None => acc // If no slot found, ignore the viva
    }.reverse // Reverse at the end to maintain the original order

    Right(())



