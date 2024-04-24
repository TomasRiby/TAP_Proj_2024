package pj.domain

import pj.domain.Availability
import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, Name}

final case class External(
                           id: ID,
                           name: Name,
                           availability: List[Availability]) extends Resource


object External:
  def from(id: ID, name: Name, availability: List[Availability]) =
    new External(id: ID, name: Name, availability: List[Availability])
  
