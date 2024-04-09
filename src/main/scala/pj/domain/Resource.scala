package pj.domain

import pj.domain.Availability

final case class Resource private(
                                   id: String,
                                   name: String,
                                   availability: Seq[Availability],
                                   resourceType: String)

object Resource:
  def from(id: String, name: String, availability: Seq[Availability], resourceType: String) =
    new Resource(id: String, name: String, availability: Seq[Availability], resourceType: String)
  
