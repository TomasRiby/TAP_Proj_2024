package pj.MS02.domain

import org.scalacheck.Prop.{AnyOperators, forAll}
import org.scalacheck.{Gen, Properties}
import pj.MS02.domain.AvailabilityTest.generateAvailabilityList
import pj.MS02.opaque.IDTest.{generateExternalID, generateID, generateTeacherID, generateUniqueIDs}
import pj.MS02.opaque.NameTest.generateName
import pj.domain.{Advisor, CoAdvisor, External, President, Supervisor, Teacher, Viva}
import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.{ID, createRegularId}

object VivaTest
  extends Properties("Testing Viva"):

  def generatePresident(ids: List[ID]): Gen[(President, List[ID])] =
    for
      chosenId <- Gen.oneOf(ids.filter(_.isTeacherId))
    yield (President.from(chosenId), ids.filterNot(_ == chosenId))

  def generateAdvisor(ids: List[ID]): Gen[(Advisor, List[ID])] =
    for
      chosenId <- Gen.oneOf(ids.filter(_.isTeacherId))
    yield (Advisor.from(chosenId), ids.filterNot(_ == chosenId))

  def generateViva(idList: List[ID]): Gen[Viva] =
    for
      student <- generateName
      title <- generateName
      (president, remainingIds) <- generatePresident(idList)
      (advisor, updatedIds) <- generateAdvisor(remainingIds)
    yield Viva.from(student, title, president, advisor, List.empty, List.empty)

  def generateVivaList(size: Int): Gen[List[Viva]] = for {
    initialIds <- generateUniqueIDs(size * 2) // Ensure enough unique IDs for all Viva
    vivaList <- Gen.listOfN(size, generateViva(initialIds))
  } yield vivaList

  // Property to test Viva list generation
  property("Each Viva in List doesnt have the same President and Advisor") = forAll(generateVivaList(50)) { vivaList =>
    // Ensure all Viva instances have unique Presidents and Advisors
    vivaList.forall(viva => viva.president.id != viva.advisor.id)
  }

