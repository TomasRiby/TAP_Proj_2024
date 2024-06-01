package pj.MS02.handleAlgorithm.ComplexGenerator

import org.scalacheck.Prop.{AnyOperators, forAll}
import org.scalacheck.{Gen, Properties}
import pj.MS02.domain.AvailabilityTest.generateAvailabilityList
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.IDTest.{generateExternalIdThatsNotOnList, generateTeacherIdThatsNotOnList}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.NameTest.generateName
import pj.domain.*
import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.{ID, createRegularId}


object VivaTest extends Properties("Testing Viva"):

  def generatePresident(ids: List[ID]): Gen[(President, List[ID])] = for {
    chosenId <- generateTeacherIdThatsNotOnList(ids)
  } yield (President.from(chosenId), chosenId :: ids)

  def generateAdvisor(ids: List[ID]): Gen[(Advisor, List[ID])] = for {
    chosenId <- generateTeacherIdThatsNotOnList(ids)
  } yield (Advisor.from(chosenId), chosenId :: ids)

  def generateCoAdvisor(ids: List[ID]): Gen[(CoAdvisor, List[ID])] = for {
    chosenId <- Gen.oneOf(generateTeacherIdThatsNotOnList(ids), generateExternalIdThatsNotOnList(ids))
  } yield (CoAdvisor.from(chosenId), chosenId :: ids)

  def generateSupervisor(ids: List[ID]): Gen[(Supervisor, List[ID])] = for {
    chosenId <- generateExternalIdThatsNotOnList(ids)
  } yield (Supervisor.from(chosenId), chosenId :: ids)

  def generateSupervisorList(ids: List[ID]): Gen[(List[Supervisor], List[ID])] =
    def helper(currentIds: List[ID], remaining: Int, acc: List[Supervisor]): Gen[(List[Supervisor], List[ID])] =
      if (remaining <= 0) Gen.const((acc, currentIds))
      else
        generateSupervisor(currentIds).flatMap { case (supervisor, updatedIds) =>
          helper(updatedIds, remaining - 1, supervisor :: acc)
        }

    for {
      count <- Gen.choose(0, 5)
      result <- helper(ids, count, List.empty)
    } yield result

  // Property to test CoAdvisors
  def generateCoAdvisorList(ids: List[ID]): Gen[(List[CoAdvisor], List[ID])] =
    def helper(currentIds: List[ID], remaining: Int, acc: List[CoAdvisor]): Gen[(List[CoAdvisor], List[ID])] =
      if (remaining <= 0) Gen.const((acc, currentIds))
      else
        generateCoAdvisor(currentIds).flatMap { case (coAdvisor, updatedIds) =>
          helper(updatedIds, remaining - 1, coAdvisor :: acc)
        }

    for {
      count <- Gen.choose(0, 5)
      result <- helper(ids, count, List.empty)
    } yield result

  def generateViva: Gen[(Viva, List[ID])] =
    val inicialList = List.empty
    for {
      student <- generateName
      title <- generateName
      (president, idsWithPresident) <- generatePresident(inicialList)
      (advisor, idsWithAdvisor) <- generateAdvisor(idsWithPresident)
      (supervisorList, idsWithSupervisors) <- generateSupervisorList(idsWithAdvisor)
      (coAdvisorList, finalIdlist) <- generateCoAdvisorList(idsWithSupervisors)
    } yield (Viva.from(student, title, president, advisor, supervisorList, coAdvisorList), finalIdlist)

  def generateVivaList(size: Int): Gen[List[Viva]] = for {
    vivaList <- Gen.listOfN(size, generateViva)
  } yield vivaList.map(_._1)

  property("Each Viva has unique IDs across President, Advisor, Supervisors, and CoAdvisors") = forAll(generateVivaList(50)) { vivaList =>
    vivaList.forall { viva =>
      val allIds = viva.president.id :: viva.advisor.id :: viva.supervisor.map(_.id) ++ viva.coAdvisor.map(_.id)
      allIds.distinct.sizeIs == allIds.size
    }
  }
    

