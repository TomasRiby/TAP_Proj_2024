package pj.domain

import pj.typeUtils.opaqueTypes.opaqueTypes.ID

final case class Resource private(
                            teacher: List[Teacher], external: List[External]
                           )

object Resource:
  def from(teacher: List[Teacher], external: List[External]) =
    new Resource(teacher: List[Teacher], external: List[External])
