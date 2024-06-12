package pj.domain

import pj.opaqueTypes.OTime.OTime

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

final case class PosViva(student: String, title: String, start: String, end: String, preference: Int, president: String, advisor: String, supervisors: List[String],coAdvisors: List[String])

object PosViva:
  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
  def from(student: String, title: String, start: String, end: String, preference: Int, president: String, advisor: String, supervisors: List[String],coAdvisors: List[String]): PosViva =
    new PosViva(student: String, title: String, start: String, end: String, preference: Int, president: String, advisor: String, supervisors: List[String],coAdvisors: List[String])
    
  def chosenAvailabilityToPosViva(start: LocalDateTime, end: LocalDateTime, preference: Int, preViva: PreViva): PosViva =
    PosViva.from(
                preViva.student.toString,
                preViva.title.toString,
                start.format(formatter),
                end.format(formatter),
                preference,
                (preViva.roleLinkedWithResourceList.collectFirst { case RoleLinkedWithResource(p: President, name, _) => name } getOrElse "").toString,
                (preViva.roleLinkedWithResourceList.collectFirst { case RoleLinkedWithResource(p: Advisor, name, _) => name } getOrElse "").toString,
                preViva.roleLinkedWithResourceList.collect { case RoleLinkedWithResource(s: Supervisor, name, _) => name.toString },
                preViva.roleLinkedWithResourceList.collect { case RoleLinkedWithResource(c: CoAdvisor, name, _) => name.toString },
              )