package pj.domain


final case class ScheduleViva private(president: RoleAvailabilities, advisor: RoleAvailabilities, supervisor: RoleAvailabilities, viva: Viva)

object ScheduleViva:
  def from(president: RoleAvailabilities, advisor: RoleAvailabilities, supervisor: RoleAvailabilities, viva: Viva) =
    new ScheduleViva(president: RoleAvailabilities, advisor: RoleAvailabilities, supervisor: RoleAvailabilities, viva: Viva)
