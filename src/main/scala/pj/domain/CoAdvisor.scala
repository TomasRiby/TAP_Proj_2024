package pj.domain

import pj.opaqueTypes.ID.ID


final case class CoAdvisor private(
                          id: ID
                        )

object CoAdvisor:
  def from(id: ID) =
    new CoAdvisor(id: ID)
