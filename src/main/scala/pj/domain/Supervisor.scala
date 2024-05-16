package pj.domain

import pj.typeUtils.opaqueTypes.opaqueTypes.ID

final case class Supervisor private(
                            id: ID
                           )

object Supervisor:
  def from(id: ID) =
    new Supervisor(id: ID)
