package pj.MS02.handleAlgorithm.ComplexGenerator

import org.scalacheck.Prop.{exists, forAll}
import org.scalacheck.{Gen, Prop, Properties}
import linkVivaWithResourceTest.generateAValidAgendaViva
import ResourceTest.{generateExternal, generateExternalListFromIDs, generateTeacherListFromIDs}
import VivaTest.{generateViva, generateVivaList}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.OTimeTest.{generateADay, generateTime}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.PreferenceTest.generatePreference
import pj.domain.*
import pj.domain.Algorithm.{algorithm, preVivaToMap}
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.OTime.OTime

import java.time.LocalDate

object PreVivaTest
  extends Properties("Testing generate Pre Viva List"):

  def generatePreVivaList: Gen[(List[PreViva], ODuration)] =
    for {
      linkedViva <- generateAValidAgendaViva
      vivaList = linkedViva._1
      teacherList = linkedViva._2
      externalList = linkedViva._3
      duration = linkedViva._4
    } yield (vivaList.map(PreViva.linkVivaWithResource(_, teacherList, externalList)), duration)