package pj.domain

final case class President private (
                           id: String
                          )

object President:
  def from(id: String) =
    new President(id: String)