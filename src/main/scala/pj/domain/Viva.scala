package pj.domain

import pj.typeUtils.opaqueTypes
import pj.typeUtils.opaqueTypes.opaqueTypes.*

final case class Viva private(
                               student: Name,
                               title: Name,
                               resources: List[Resource]
                             )

object Viva:
  //Abaixo definimos as funções
  def from(student: Name, title: Name, resources: List[Resource]) =
    new Viva(student: Name, title: Name, resources: List[Resource])


