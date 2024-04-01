package pj.domain

import pj.typeUtils.integer.NonNegativeInt


final case class Availability(
                               start: String,
                               end: String,
                               preference: String
                             )

object Availability:
  //Abaixo definimos as funções
  def funcs() = ()



