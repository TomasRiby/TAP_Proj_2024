package pj.domain

import pj.opaqueTypes.Name.Name

final case class PreViva private(title:Name, student:Name,roleLinkedWithResourceList: List[RoleLinkedWithResource])

object PreViva:
  def from(title:Name, student:Name,roleLinkedWithResourceList: List[RoleLinkedWithResource]) =
    new PreViva(title:Name, student:Name,roleLinkedWithResourceList: List[RoleLinkedWithResource])

  def linkVivaWithResource(viva: Viva, teacherList: List[Teacher], externalList: List[External]): PreViva =
      val president = viva.president
      val advisor = viva.advisor
      val coAdvisorList = viva.coAdvisor
      val supervisorList = viva.supervisor
      val student = viva.student
      val title = viva.title

      val presidentRoles = for {
        presidentTeacher <- teacherList if presidentTeacher.id == president.id
      } yield RoleLinkedWithResource.from(president, presidentTeacher.name, presidentTeacher.availability)

      val advisorRoles = for {
        advisorTeacher <- teacherList if advisorTeacher.id == advisor.id
      } yield RoleLinkedWithResource.from(advisor, advisorTeacher.name, advisorTeacher.availability)

      val supervisorRoles = for {
        supervisor <- supervisorList
        external <- externalList if external.id == supervisor.id
      } yield RoleLinkedWithResource.from(supervisor, external.name, external.availability)

      val coAdvisorExternal = for {
        coAdvisor <- coAdvisorList
        external <- externalList if external.id == coAdvisor.id
      } yield RoleLinkedWithResource.from(coAdvisor, external.name, external.availability)

      val coADvisorTeacher = for {
        coAdvisor <- coAdvisorList
        teacher <- teacherList if teacher.id == coAdvisor.id
      } yield RoleLinkedWithResource.from(coAdvisor, teacher.name, teacher.availability)

      PreViva.from(student, title, presidentRoles ++ advisorRoles ++ supervisorRoles ++ coAdvisorExternal ++ coADvisorTeacher)