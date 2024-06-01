package pj.MS02.handleAlgorithm.ComplexGenerator

import org.scalacheck.Prop.{exists, forAll}
import org.scalacheck.{Gen, Prop, Properties}
import linkVivaWithResourceTest.generateAValidAgendaViva
import ResourceTest.{generateExternal, generateTeacherListFromIDs}
import VivaTest.{generateViva, generateVivaList}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.OTimeTest.{generateADay, generateTime}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.PreferenceTest.generatePreference
import pj.domain.*
import pj.domain.Algorithm.{algorithmFCFS, preVivaToMap}
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.OTime.OTime

import java.time.LocalDate

object PreVivaTest
  extends Properties("Testing generate Pre Viva List"):

  def generatePreVivaList: Gen[(List[PreViva], ODuration, List[Availability])] =
    for {
      linkedViva <- generateAValidAgendaViva
      vivaList = linkedViva._1
      teacherList = linkedViva._2
      externalList = linkedViva._3
      duration = linkedViva._4
      availListGenerated = linkedViva._5
      preVivaList = vivaList.map(PreViva.linkVivaWithResource(_, teacherList, externalList))
    } yield (preVivaList, duration, availListGenerated)


  property("Each PreViva in the generated list is valid") = forAll(generatePreVivaList) { case (preVivaList, duration, availList) =>
    preVivaList.lengthIs == availList.length
  }

  property("No list of PreVivas is empty") = forAll(generatePreVivaList) { case (preVivaList, duration, availList) =>
    preVivaList.nonEmpty && availList.nonEmpty
  }

  property("Each PreViva contains at least one availability from availListGenerated") = forAll(generatePreVivaList) { case (preVivaList, duration, availList) =>
    preVivaList.forall { preViva =>
      preViva.roleLinkedWithResourceList.exists { role =>
        role.listAvailability.exists(availList.contains)
      }
    }
  }

