package pj.domain

import pj.domain.Availability
import pj.typeUtils.ResourceType

final case class External private(
                                   id: String,
                                   name: String,
                                   availability: Seq[Availability])


object External:
  def from(id: String, name: String, availability: Seq[Availability]) =
    new External(id: String, name: String, availability: Seq[Availability])
  
