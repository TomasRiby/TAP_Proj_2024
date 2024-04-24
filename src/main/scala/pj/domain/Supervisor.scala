package pj.domain

import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, Name}
import pj.domain.Resource

final case class Supervisor private(
                                 id: ID,
                                 name: Name,
                                 availability: List[Availability]
                           ) extends Resource

object Supervisor:
  def from(id: ID, name: Name, availability: List[Availability]) =
    new Supervisor(id: ID, name: Name, availability: List[Availability])
