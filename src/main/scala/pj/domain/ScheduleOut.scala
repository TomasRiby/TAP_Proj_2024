package pj.domain

import pj.opaqueTypes.Preference.Preference

final case class ScheduleOut private(preference: Int, vivas: List[PosViva])

object ScheduleOut:
  def from(): ScheduleOut =
    val posViva1 = PosViva.from("Student 001", "Title 1", "2024-05-30T10:30:00", "2024-05-30T11:30:00", 12, "Teacher 001", "Teacher 002", List("External 001"), List())
    val posViva2 = PosViva.from("Student 002", "Title 2", "2024-05-30T15:30:00", "2024-05-30T16:30:00", 13, "Teacher 002", "Teacher 001", List("External 001"), List())
    val preference = posViva1.preference + posViva2.preference
    new ScheduleOut(preference, List(posViva1, posViva2))