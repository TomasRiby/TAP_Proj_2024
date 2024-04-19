package pj.domain

import pj.typeUtils.opaqueTypes.opaqueTypes.ID

final case class VivaResult private(availabilities: Availability, president: Teacher, advisor: Teacher, supervisor: List[External])

object VivaResult:
  def from(availabilities: Availability, president: Teacher, advisor: Teacher, supervisor: List[External]) =
    new VivaResult(availabilities: Availability, president: Teacher, advisor: Teacher, supervisor: List[External])
