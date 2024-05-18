package pj.domain

import pj.opaqueTypes.Name.Name

final case class RoleLinkedWithResource private(role: President | Advisor | Supervisor | CoAdvisor, name: Name, listAvailability: List[Availability])

object RoleLinkedWithResource:
  def from(role: President | Advisor | Supervisor | CoAdvisor, name: Name, listAvailability: List[Availability]) =
    new RoleLinkedWithResource(role: President | Advisor | Supervisor | CoAdvisor, name: Name, listAvailability: List[Availability])