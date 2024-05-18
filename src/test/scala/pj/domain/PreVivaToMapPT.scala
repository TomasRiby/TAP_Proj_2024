import org.scalacheck.*
import org.scalacheck.Prop.forAll
import pj.domain.{Algorithm, Availability, PreViva, RoleLinkedWithResource}

object PreVivaToMapPT extends Properties("PreViva") {

  // Definindo um gerador para PreViva
  val genPreVivaList: Gen[List[PreViva]] = Gen.listOfN(1, Generator.genPreViva)

  // Função auxiliar para extrair todas as disponibilidades de uma lista de RoleLinkedWithResource
  def extractAvailabilities(roles: List[RoleLinkedWithResource]): List[List[Availability]] =
    roles.map(_.listAvailability)

  // Teste de propriedade para o método preVivaToMap
  property("preVivaToMap should maintain role-availability correspondence") = forAll(genPreVivaList) { vivaList =>
    val result = Algorithm.preVivaToMap(vivaList)

    // Mapeando cada conjunto de roles para suas disponibilidades correspondentes nos dados de entrada
    val expected = vivaList.map { preViva =>
      val roles = preViva.roleLinkedWithResourceList.map(_.role).toSet
      roles -> preViva.roleLinkedWithResourceList.map(_.listAvailability)
    }.toMap

    // Verificando se o resultado é igual ao esperado
    result == expected
  }

  property("preVivaToMap should not modify original input data") = forAll(genPreVivaList) { vivaList =>
    val vivamap = Algorithm.preVivaToMap(vivaList)

    // Verificando se cada elemento individual dentro da lista de PreViva permanece inalterado
    val allUnchanged = vivaList.forall { originalPreViva =>
      // Encontre o mesmo objeto PreViva na lista após a execução da função
      val resultPreViva = vivaList.find(_.equals(originalPreViva))

      // Verifique se os dois objetos PreViva são o mesmo na memória
      resultPreViva.contains(originalPreViva)
    }

    allUnchanged
  }

  property("preVivaToMap should maintain correct availabilities for each role") = forAll(genPreVivaList) { vivaList =>
    val result = Algorithm.preVivaToMap(vivaList)

    // Verificando se as disponibilidades no resultado correspondem às disponibilidades dos RoleLinkedWithResource em cada PreViva
    val expected = vivaList.map { preViva =>
      val roles = preViva.roleLinkedWithResourceList.map(_.role).toSet
      roles -> extractAvailabilities(preViva.roleLinkedWithResourceList)
    }.toMap

    result == expected
  }
}