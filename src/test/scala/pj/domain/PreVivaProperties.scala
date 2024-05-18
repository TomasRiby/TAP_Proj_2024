import org.scalacheck.*
import org.scalacheck.Prop.forAll
import pj.domain.{Algorithm, PreViva}

object PreVivaSpec extends Properties("PreViva") {

  // Definindo um gerador para PreViva
  val genPreVivaList: Gen[List[PreViva]] = Gen.listOfN(1, Generator.genPreViva)

  // Teste de propriedade para o método preVivaToMap
  property("preVivaToMap should maintain role-availability correspondence") = forAll(genPreVivaList) { vivaList =>
    // Executando o método a ser testado
    val result = Algorithm.preVivaToMap(vivaList)

    // Mapeando cada conjunto de roles para suas disponibilidades correspondentes nos dados de entrada
    val expected = vivaList.map { preViva =>
      val roles = preViva.roleLinkedWithResourceList.map(_.role).toSet
      roles -> preViva.roleLinkedWithResourceList.map(_.listAvailability)
    }.toMap

    // Verificando se o resultado é igual ao esperado
    val testPassed = result == expected
    if (!testPassed)
      println(s"Failed test with vivaList: $vivaList")
      println(s"Expected: $expected")
      println(s"Result: $result")

    testPassed
  }
}