package pj.domain


final case class ScheduleViva private(president: RoleAvailabilities, advisor: RoleAvailabilities, supervisor: RoleAvailabilities)

object ScheduleViva:
  def from(president: RoleAvailabilities, advisor: RoleAvailabilities, supervisor: RoleAvailabilities) =
    new ScheduleViva(president: RoleAvailabilities, advisor: RoleAvailabilities, supervisor: RoleAvailabilities)
