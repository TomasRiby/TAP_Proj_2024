package pj.domain

import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name.Name

import scala.collection.immutable.HashSet

final case class PreViva private(student: Name, title: Name, roleLinkedWithResourceList: List[RoleLinkedWithResource])

object PreViva:
  def from(student: Name, title: Name, roleLinkedWithResourceList: List[RoleLinkedWithResource]) =
    new PreViva(student: Name, title: Name, roleLinkedWithResourceList: List[RoleLinkedWithResource])

  def hashSetOfIds(preViva: PreViva): HashSet[ID] =
    val ids = preViva.roleLinkedWithResourceList.map(_.role).collect:
      case President(id) => id
      case Advisor(id) => id
      case Supervisor(id) => id
      case CoAdvisor(id) => id
    ids.to(HashSet)

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

    val coAdvisorTeacher = for {
      coAdvisor <- coAdvisorList
      teacher <- teacherList if teacher.id == coAdvisor.id
    } yield RoleLinkedWithResource.from(coAdvisor, teacher.name, teacher.availability)

    val coAdvisorExternal = for {
      coAdvisor <- coAdvisorList
      external <- externalList if external.id == coAdvisor.id
    } yield RoleLinkedWithResource.from(coAdvisor, external.name, external.availability)


    PreViva.from(student, title, presidentRoles ++ advisorRoles ++ supervisorRoles ++ coAdvisorTeacher ++ coAdvisorExternal)