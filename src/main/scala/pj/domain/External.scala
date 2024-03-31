package pj.domain

final case class External(
                           id: String,
                           name: String,
                           availability: Seq[Availability]
                         )

object External:
  //Abaixo definimos as funções
  def funcs() = ()


