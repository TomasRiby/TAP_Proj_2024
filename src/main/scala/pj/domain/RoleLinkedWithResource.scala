package pj.domain

import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name.Name

final case class RoleLinkedWithResource private(role: President | Advisor | Supervisor | CoAdvisor, name: Name, listAvailability: List[Availability])

extension (r: RoleLinkedWithResource)
  def getRoleId: ID = RoleLinkedWithResource.roleId(r.role)

object RoleLinkedWithResource:
  def from(role: President | Advisor | Supervisor | CoAdvisor, name: Name, listAvailability: List[Availability]) =
    new RoleLinkedWithResource(role: President | Advisor | Supervisor | CoAdvisor, name: Name, listAvailability: List[Availability])
    
  def roleId(role: President | Advisor | Supervisor | CoAdvisor): ID = role match
    case President(id) => id
    case Advisor(id) => id
    case Supervisor(id) => id
    case CoAdvisor(id) => id
