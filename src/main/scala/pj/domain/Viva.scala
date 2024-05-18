package pj.domain

import pj.opaqueTypes.Name.Name


final case class Viva private(student: Name, title: Name, president: President, advisor: Advisor, supervisor: List[Supervisor], coAdvisor: List[CoAdvisor])

object Viva:
  //Abaixo definimos as funções
  def from(student: Name, title: Name, president: President, advisor: Advisor, supervisor: List[Supervisor], coAdvisor: List[CoAdvisor]) =
    new Viva(student: Name, title: Name, president: President, advisor: Advisor, supervisor: List[Supervisor], coAdvisor: List[CoAdvisor])


