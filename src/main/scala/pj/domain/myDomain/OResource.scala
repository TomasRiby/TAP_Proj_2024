package pj.domain.schedule

import pj.domain.{Availability, External, OExternal, OTeacher, Teacher}
import pj.io.ResourceIO
import pj.opaqueTypes.OID.OID
import pj.opaqueTypes.OName.OName

final case class OResource private(
                                   teacher: List[OTeacher], external: List[OExternal]
                                 )

object OResource:
  def from(teacher: List[OTeacher], external: List[OExternal]) =
    new OResource(teacher: List[OTeacher], external: List[OExternal])
