package pj.domain

import pj.typeUtils.opaqueTypes.opaqueTypes.ID

final case class RoleAvailabilities private(id: Any, availabilities: List[(ID, List[Availability])]) {}

object RoleAvailabilities:
  def from(id: Any, availabilities: List[(ID, List[Availability])]) =
    new RoleAvailabilities(id: Any, availabilities: List[(ID, List[Availability])])
