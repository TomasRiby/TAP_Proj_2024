package pj.domain


import pj.opaqueTypes.OTime.OTime

import java.time.{Duration, LocalDateTime}


object AlgorithmV2:
  def makeTheAlgorithmHappen(agenda: Agenda): Result[Unit] =
    val teacherList = agenda.resources.teacher
    val externalList = agenda.resources.external

    val preVivaList = agenda.vivas.map(PreViva.linkVivaWithResource(_, teacherList, externalList))

    def getCommonAvailability(preViva: PreViva): Unit =
      val allAvailabilities = preViva.roleLinkedWithResourceList.flatMap:
        case RoleLinkedWithResource(_, resource: Teacher) => resource.availability
        case RoleLinkedWithResource(_, resource: External) => resource.availability
      println(allAvailabilities)

    val sortedPreVivaList = preVivaList.foreach(getCommonAvailability)
    

    Right(())


