package pj.domain

import pj.domain.ScheduleViva
import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, ODuration, Preference}

import java.time.{Duration, LocalDateTime}
import scala.::
import scala.annotation.tailrec
import scala.collection.immutable.Nil.:::

object AlgorithmV2:
  def makeTheAlgorithmHappen(agenda: Agenda): Result[Unit] =

    val teacherList = agenda.resources.teacher
    val externalList = agenda.resources.external

    def linkVivaWithResource(viva: Viva): PreViva =
      val president = viva.president
      val advisor = viva.advisor
      val coAdvisorList = viva.coAdvisor
      val supervisorList = viva.supervisor

      val presidentRoles = for {
        presidentTeacher <- teacherList if presidentTeacher.id == president.id
      } yield RoleLinkedWithResource.from(president, presidentTeacher)

      val advisorRoles = for {
        advisorTeacher <- teacherList if advisorTeacher.id == advisor.id
      } yield RoleLinkedWithResource.from(advisor, advisorTeacher)

      val supervisorRoles = for {
        supervisor <- supervisorList
        external <- externalList if external.id == supervisor.id
      } yield RoleLinkedWithResource.from(supervisor, external)

      val coAdvisor = for {
        coAdvisor <- coAdvisorList
        external <- externalList if external.id == coAdvisor.id
      } yield RoleLinkedWithResource.from(coAdvisor, external)
      
      PreViva.from(presidentRoles ++ advisorRoles ++ supervisorRoles ++ coAdvisor)


    val preViva = agenda.vivas.map(linkVivaWithResource)

    preViva.foreach(node =>
      println("---------------------------")
      println(node))

    Right(())


