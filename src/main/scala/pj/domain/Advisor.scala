package pj.domain

import pj.typeUtils.opaqueTypes.opaqueTypes.ID

final case class Advisor private(
                          id: ID
                        )

object Advisor:
  def from(id: ID) =
    new Advisor(id: ID)
