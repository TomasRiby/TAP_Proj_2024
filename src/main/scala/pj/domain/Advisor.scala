package pj.domain

final case class Advisor private(
                          id: String
                        )

object Advisor:
  def from(id: String) =
    new Advisor(id: String)
