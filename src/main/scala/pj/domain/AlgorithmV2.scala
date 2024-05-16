package pj.domain

import pj.domain.ScheduleViva
import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, ODuration, Preference}

import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec

object AlgorithmV2:
  def makeTheAlgorithmHappen(agenda: Agenda): Result[Unit] =


    val presidentList = agenda.vivas.map(viva => viva.president)
    val advisorList = agenda.vivas.map(viva => viva.advisor)
    val supervisorList = agenda.vivas.map(viva => viva.supervisor)
    val coAdvisorList = agenda.vivas.map(viva => viva.coAdvisor)

    val teacherList = agenda.resources.teacher
    val externalList = agenda.resources.external

    final case class VivaLinkedWithResource private(role: President | Advisor | Supervisor | CoAdvisor, resource: Teacher | External)

    object VivaLinkedWithResource:
      def from(role: President | Advisor | Supervisor | CoAdvisor, resource: Teacher | External) =
        new VivaLinkedWithResource(role: President | Advisor | Supervisor | CoAdvisor, resource: Teacher | External)
    
    val presidentTeachers = presidentList.flatMap(presidentID => findTeacherById(teacherList, presidentID))
    Right(())

