package pj.domain

import pj.opaqueTypes.Preference
import pj.opaqueTypes.Preference.Preference

final case class ScheduleOut private(preference: Preference, posVivas: List[PosViva])

object ScheduleOut:
  def from(vivas: List[PosViva]): ScheduleOut =
    new ScheduleOut(preference = Preference.fromMoreThan5(vivas.map(_.preference).sum), posVivas = vivas)
    