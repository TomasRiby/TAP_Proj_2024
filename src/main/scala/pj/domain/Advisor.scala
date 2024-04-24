package pj.domain

import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, Name}

final case class Advisor private(
                            id: ID,
                            name: Name,
                            availability: List[Availability]
                        ) extends Resource

object Advisor:
  def from(id: ID, name: Name, availability: List[Availability]) =
    new Advisor(id: ID, name: Name, availability: List[Availability])
