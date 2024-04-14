package pj.domain

import pj.typeUtils.opaqueTypes
import pj.typeUtils.opaqueTypes.opaqueTypes.*

final case class Viva private(
                               student: Name,
                               title: Name,
                               president: President,
                               advisor: Advisor,
                               supervisor: Supervisor
                             )

object Viva:
  //Abaixo definimos as funções
  def from(student: Name, title: Name, president: President, advisor: Advisor, supervisor: Supervisor) =
    new Viva(student: Name, title: Name, president: President, advisor: Advisor, supervisor: Supervisor)


