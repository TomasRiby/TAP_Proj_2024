package pj.domain

final case class PreViva private(roleLinkedWithResourceList: List[RoleLinkedWithResource])

object PreViva:
  def from(roleLinkedWithResourceList: List[RoleLinkedWithResource]) =
    new PreViva(roleLinkedWithResourceList)

  def linkVivaWithResource(viva: Viva, teacherList: List[Teacher], externalList: List[External]): PreViva =
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