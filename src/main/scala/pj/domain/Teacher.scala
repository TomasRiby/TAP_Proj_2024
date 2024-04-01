package pj.domain

import scala.xml.Node

final case class Teacher(
                          id: String,
                          name:String,
                          availability: Seq[Availability]
                          )

object Teacher:
  //Abaixo definimos as funções
  def funcs(): Nothing = ???




