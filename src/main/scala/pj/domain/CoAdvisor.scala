package pj.domain

import pj.typeUtils.opaqueTypes.opaqueTypes.ID

final case class CoAdvisor private(
                          id: ID
                        )

object CoAdvisor:
  def from(id: ID) =
    new CoAdvisor(id: ID)
