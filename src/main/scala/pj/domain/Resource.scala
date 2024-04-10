package pj.domain

import pj.domain.Availability
import pj.typeUtils.ResourceType

final case class Resource private(
                                   id: String,
                                   name: String,
                                   availability: Seq[Availability],
                                   resourceType: ResourceType)

object Resource:
  def from(id: String, name: String, availability: Seq[Availability], resourceType: ResourceType) =
    new Resource(id: String, name: String, availability: Seq[Availability], resourceType: ResourceType)
  
