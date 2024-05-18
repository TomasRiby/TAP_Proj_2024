package pj.domain

import pj.opaqueTypes.ID.ID


final case class President private(
                                    id: ID
                                  )

object President:


  def from(id: ID): President =
    new President(id)

