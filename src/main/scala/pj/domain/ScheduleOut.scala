package pj.domain

import pj.opaqueTypes.Preference.Preference

final case class ScheduleOut private(preference: Preference, vivas: List[PosViva])

object ScheduleOut:
  def from(preference: Preference, vivas: List[PosViva]) =
    new ScheduleOut(preference: Preference, vivas: List[PosViva])