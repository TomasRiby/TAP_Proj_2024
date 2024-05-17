package pj.domain

import pj.domain.Availability
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name.Name

final case class External(
                           id: ID,
                           name: Name,
                           availability: List[Availability])


object External:
  def from(id: ID, name: Name, availability: List[Availability]) =
    new External(id: ID, name: Name, availability: List[Availability])
  
