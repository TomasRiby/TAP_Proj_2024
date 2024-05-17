package pj.domain

import pj.opaqueTypes.OTime.OTime

final case class PosViva(student: String, title: String, start: String, end: String, preference: Int, president: String, advisor: String, supervisor: String)

object PosViva:
  def from(student: String, title: String, start: String, end: String, preference: Int, president: String, advisor: String, supervisor: String): PosViva =
    new PosViva(student, title, start, end, preference, president, advisor, supervisor)