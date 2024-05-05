package pj.domain

import pj.domain.Availability
import pj.domain.myDomain.OAvailability
import pj.io.ResourceIO
import pj.opaqueTypes.OID.OID
import pj.opaqueTypes.OName.OName

final case class OExternal private(
                                    id: OID,
                                    name: OName,
                                    availability: List[OAvailability])


object OExternal:
  def from(id: OID, name: OName, availability: List[OAvailability]): OExternal =
    new OExternal(id: OID, name: OName, availability: List[OAvailability])
