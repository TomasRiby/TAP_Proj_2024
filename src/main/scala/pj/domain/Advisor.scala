package pj.domain

import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.ID

final case class Advisor private(
                          id: ID
                        )

object Advisor:
  def from(id: ID) =
    new Advisor(id: ID)
  
  extension (advisor: Advisor)
    def isValid: Boolean = advisor.id.isTeacherId

