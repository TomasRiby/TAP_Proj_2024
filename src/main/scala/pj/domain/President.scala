package pj.domain

import pj.typeUtils.opaqueTypes.opaqueTypes.ID

final case class President private(
                                    id: ID
                                  )

object President:


  def from(id: ID): President =
    new President(id)

