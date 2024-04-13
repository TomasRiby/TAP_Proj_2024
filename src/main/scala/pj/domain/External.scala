package pj.domain

import pj.domain.Availability
import pj.typeUtils.ResourceType
import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, Name}

final case class External(
                           id: ID,
                           name: Name,
                           availability: Seq[Availability])


object External:
  def from(id: ID, name: Name, availability: Seq[Availability]) =
    new External(id: ID, name: Name, availability: Seq[Availability])
  
