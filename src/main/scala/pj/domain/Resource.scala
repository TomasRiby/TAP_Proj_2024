package pj.domain

import pj.domain.{ External, Teacher}
import pj.io.ResourceIO
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name.Name

final case class Resource private(
                                    teacher: List[Teacher], external: List[External]
                                 )

object Resource:
  def from(teacher: List[Teacher], external: List[External]) =
    new Resource(teacher: List[Teacher], external: List[External])
