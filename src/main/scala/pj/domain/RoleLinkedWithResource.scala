package pj.domain

final case class RoleLinkedWithResource private(role: President | Advisor | Supervisor | CoAdvisor, resource: Teacher | External)

object RoleLinkedWithResource:
  def from(role: President | Advisor | Supervisor | CoAdvisor, resource: Teacher | External) =
    new RoleLinkedWithResource(role: President | Advisor | Supervisor | CoAdvisor, resource: Teacher | External)