package pj.domain

final case class Supervisor private(
                            id: String,
                           )

object Supervisor:
  def from(id: String) =
    new Supervisor(id: String)
