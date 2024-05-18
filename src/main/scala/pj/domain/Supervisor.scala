package pj.domain

import pj.opaqueTypes.ID.ID


final case class Supervisor private(
                            id: ID
                           )

object Supervisor:
  def from(id: ID) =
    new Supervisor(id: ID)
    
  extension (supervisor: Supervisor)
    def isValid: Boolean = supervisor.id.isExternalId
