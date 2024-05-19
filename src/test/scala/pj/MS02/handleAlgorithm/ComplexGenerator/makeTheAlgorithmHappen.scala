package pj.MS02.handleAlgorithm.ComplexGenerator

import org.scalacheck.Prop.{exists, forAll}
import org.scalacheck.{Gen, Prop, Properties}
import pj.MS02.handleAlgorithm.ComplexGenerator.ResourceTest.{generateExternal, generateExternalListFromIDs, generateTeacherListFromIDs}
import pj.MS02.handleAlgorithm.ComplexGenerator.VivaTest.{generateViva, generateVivaList}
import pj.MS02.handleAlgorithm.ComplexGenerator.linkVivaWithResourceTest.generateAValidAgendaViva
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.OTimeTest.{generateADay, generateTime}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.PreferenceTest.generatePreference
import pj.domain.*
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.OTime.OTime

import java.time.LocalDate

object makeTheAlgorithmHappen
  extends Properties("Testing The Algorithm"):

  def generatePreViva: Gen[(List[PreViva],ODuration)] =
    for {
      linkedViva <- generateAValidAgendaViva
      vivaList = linkedViva._1
      teacherList = linkedViva._2
      externalList = linkedViva._3
      duration = linkedViva._4
    } yield (vivaList.map(PreViva.linkVivaWithResource(_, teacherList, externalList)),duration)


  property("Putting the generated PreVivas in the algorithm") = forAll(generatePreViva) { list =>
    true
  }