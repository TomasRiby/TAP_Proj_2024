package pj.domain

import pj.opaqueTypes.OTime.OTime

final case class PosViva(student: String, title: String, start: String, end: String, preference: Int, president: String, advisor: String, supervisors: List[String],coAdvisors: List[String])

object PosViva:
  def from(student: String, title: String, start: String, end: String, preference: Int, president: String, advisor: String, supervisors: List[String],coAdvisors: List[String]): PosViva =
    new PosViva(student: String, title: String, start: String, end: String, preference: Int, president: String, advisor: String, supervisors: List[String],coAdvisors: List[String])