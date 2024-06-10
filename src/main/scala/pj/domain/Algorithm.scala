package pj.domain


import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.{OTime, Preference}
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference.Preference

import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec


object Algorithm:
  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  def makeTheAlgorithmHappen(agenda: Agenda): Result[ScheduleOut] =
    val teacherList = agenda.resources.teacher
    val externalList = agenda.resources.external
    val duration = agenda.duration

    val preVivaList = agenda.vivas.map(PreViva.linkVivaWithResource(_, teacherList, externalList))

    val availabilityMap = preVivaToMap(preVivaList)

    algorithmBST(preVivaList.toList, availabilityMap, duration)

  def preVivaToMap(vivaList: Seq[PreViva]): Map[Set[President | Advisor | Supervisor | CoAdvisor], List[List[Availability]]] =
    val roles = vivaList.map(viva => viva.roleLinkedWithResourceList.map(_.role).toSet)
    val availabilities = vivaList.map(viva => viva.roleLinkedWithResourceList.map(_.listAvailability))
    roles.zip(availabilities).toMap

  def algorithmFCFS(preVivaList: List[PreViva], availabilityMap: Map[Set[President | Advisor | Supervisor | CoAdvisor], List[List[Availability]]], duration: ODuration): Result[ScheduleOut] =
    val schedulingResult = preVivaList.foldLeft[Result[(List[PosViva], List[Availability])]](Right((List.empty[PosViva], List.empty[Availability]))):
      case (accResult, viva) =>
        accResult.flatMap { case (acc, usedSlots) =>
          val roleSet = viva.roleLinkedWithResourceList.map(_.role).toSet
          val availabilities = availabilityMap.getOrElse(roleSet, List.empty)
          val possibleSlots = Availability.findAllPossibleAvailabilitiesSlot(availabilities, duration)
          val updatedPossibleSlots = Availability.updateAvailabilitySlots(possibleSlots, duration, usedSlots)
          val (chosenSlotOpt, updatedUsedSlots) = Availability.chooseFirstPossibleAvailabilitiesSlot(updatedPossibleSlots, duration, usedSlots)
          chosenSlotOpt match
            case Some((start, end, preference)) =>
              val scheduledViva = PosViva(
                viva.student.toString,
                viva.title.toString,
                start.format(formatter),
                end.format(formatter),
                preference,
                (viva.roleLinkedWithResourceList.collectFirst { case RoleLinkedWithResource(p: President, name, _) => name } getOrElse "").toString,
                (viva.roleLinkedWithResourceList.collectFirst { case RoleLinkedWithResource(p: Advisor, name, _) => name } getOrElse "").toString,
                viva.roleLinkedWithResourceList.collect { case RoleLinkedWithResource(s: Supervisor, name, _) => name.toString },
                viva.roleLinkedWithResourceList.collect { case RoleLinkedWithResource(c: CoAdvisor, name, _) => name.toString },
              )
              Right((scheduledViva :: acc, updatedUsedSlots))
            case None =>
              Left(DomainError.ImpossibleSchedule)
        }
    schedulingResult.map { case (scheduledVivas, _) =>
      val sortedScheduledVivas = scheduledVivas.sortBy(v => LocalDateTime.parse(v.start, formatter))
      ScheduleOut.from(sortedScheduledVivas)
    }

  // Define a simple binary search tree for Availability based on the preference
  case class BSTNode(value: Availability, left: Option[BSTNode], right: Option[BSTNode])
  
  def insertNode(root: Option[BSTNode], value: Availability): BSTNode = root match
    case None => BSTNode(value, None, None)
    case Some(node) =>
      if value.preference > node.value.preference then
        node.copy(right = Some(insertNode(node.right, value)))
      else
        node.copy(left = Some(insertNode(node.left, value)))
  
  def buildBST(availabilities: List[Availability]): Option[BSTNode] =
    availabilities.foldLeft[Option[BSTNode]](None) { (tree, availability) =>
      Some(insertNode(tree, availability))
    }
  
  def sumHighestPreferences(node: Option[BSTNode], count: Int): Int = node match
    case None => 0
    case Some(n) =>
      val rightSum = sumHighestPreferences(n.right, count)
      if rightSum >= count then
        rightSum
      else
        rightSum + n.value.preference + sumHighestPreferences(n.left, count - rightSum - 1)
  
  def algorithmBST(preVivaList: List[PreViva], availabilityMap: Map[Set[President | Advisor | Supervisor | CoAdvisor], List[List[Availability]]], duration: ODuration): Result[ScheduleOut] =
    val schedulingResult = preVivaList.foldLeft[Result[(List[PosViva], List[Availability])]](Right((List.empty[PosViva], List.empty[Availability]))):
      case (accResult, viva) =>
        accResult.flatMap { case (acc, usedSlots) =>
          val roleSet = viva.roleLinkedWithResourceList.map(_.role).toSet
          val availabilities = availabilityMap.getOrElse(roleSet, List.empty)
          val possibleSlots = Availability.findAllPossibleAvailabilitiesSlot(availabilities, duration)
          val updatedPossibleSlots = Availability.updateAvailabilitySlots(possibleSlots, duration, usedSlots)
          println(updatedPossibleSlots)
  
          // Implementando a BST e somando as maiores preferências
          val bst = buildBST(updatedPossibleSlots)
          val sumOfHighestPreferences = sumHighestPreferences(bst, updatedPossibleSlots.length)
          println(s"Sum of highest preferences for slots: $sumOfHighestPreferences")
  
          val (chosenSlotOpt, updatedUsedSlots) = Availability.chooseFirstPossibleAvailabilitiesSlot(updatedPossibleSlots, duration, usedSlots)
          chosenSlotOpt match
            case Some((start, end, preference)) =>
              val scheduledViva = PosViva(
                viva.student.toString,
                viva.title.toString,
                start.format(formatter),
                end.format(formatter),
                preference,
                (viva.roleLinkedWithResourceList.collectFirst { case RoleLinkedWithResource(p: President, name, _) => name } getOrElse "").toString,
                (viva.roleLinkedWithResourceList.collectFirst { case RoleLinkedWithResource(p: Advisor, name, _) => name } getOrElse "").toString,
                viva.roleLinkedWithResourceList.collect { case RoleLinkedWithResource(s: Supervisor, name, _) => name.toString },
                viva.roleLinkedWithResourceList.collect { case RoleLinkedWithResource(c: CoAdvisor, name, _) => name.toString },
              )
              Right((scheduledViva :: acc, updatedUsedSlots))
            case None =>
              Left(DomainError.ImpossibleSchedule)
        }
    schedulingResult.map { case (scheduledVivas, _) =>
      val sortedScheduledVivas = scheduledVivas.sortBy(v => LocalDateTime.parse(v.start, formatter))
      ScheduleOut.from(sortedScheduledVivas)
    }