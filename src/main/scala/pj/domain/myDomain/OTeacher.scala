package pj.domain

import pj.domain.Availability
import pj.domain.myDomain.OAvailability
import pj.io.ResourceIO
import pj.opaqueTypes.OID.OID
import pj.opaqueTypes.OName.OName

final case class OTeacher private(
                                   id: OID,
                                   name: OName,
                                   availability: List[OAvailability])



object OTeacher:
  def from(id: OID, name: OName, availability: List[OAvailability]):OTeacher =
    new OTeacher(id: OID, name: OName, availability: List[OAvailability])

