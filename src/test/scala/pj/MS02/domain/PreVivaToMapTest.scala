package pj.MS02.domain

import org.scalacheck.*
import org.scalacheck.Prop.forAll
import pj.domain.{Algorithm, Availability, PreViva, RoleLinkedWithResource}

object PreVivaToMapTest extends Properties("PreViva") {

  // Definindo um gerador para PreViva
  val genPreVivaList: Gen[List[PreViva]] = Gen.listOfN(1, Generator.genPreViva)

  // Função auxiliar para extrair todas as disponibilidades de uma lista de RoleLinkedWithResource
  def extractAvailabilities(roles: List[RoleLinkedWithResource]): List[List[Availability]] =
    roles.map(_.listAvailability)
  
}