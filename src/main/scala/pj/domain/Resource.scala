package pj.domain

import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, Name}

final case class Resource private(
                            id: ID, name: Name, availabilities: List[Availability], resourceType: ResourseType
                           )

object Resource:
  def from(id: ID, name: Name, availabilities: List[Availability], resourceType: ResourseType) =
    new Resource(id, name, availabilities, resourceType)
