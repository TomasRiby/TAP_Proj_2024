package pj.domain

final case class PreViva private(roleLinkedWithResourceList: List[RoleLinkedWithResource])

object PreViva:
  def from(roleLinkedWithResourceList: List[RoleLinkedWithResource]) =
    new PreViva(roleLinkedWithResourceList)
