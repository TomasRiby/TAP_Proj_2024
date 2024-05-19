package pj.MS02.handleAlgorithm

import org.scalacheck.Prop.{exists, forAll}
import org.scalacheck.{Gen, Prop, Properties}
import pj.MS02.domain.ResourceTest.{generateExternal, generateExternalListFromIDs, generateTeacherListFromIDs}
import pj.MS02.domain.VivaTest.{generateViva, generateVivaList}
import pj.MS02.opaque.OTimeTest.{generateADay, generateTime}
import pj.MS02.opaque.PreferenceTest.generatePreference
import pj.domain.{Availability, External, PreViva, Result, Teacher, Viva}
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.OTime.OTime

import java.time.LocalDate

object linkVivaWithResourceTest
  extends Properties("Testing linkVivaWithResource"):
  def generateVivaWithTeachersAndExternals(day: LocalDate): Gen[(Viva, List[Teacher], List[External])] =
    for {
      vivaGenerated <- generateViva
      viva = vivaGenerated._1
      listOfIDs = vivaGenerated._2
      listOfTeacherIds = listOfIDs.filter(_.isTeacherId).distinct
      listTeachers <- generateTeacherListFromIDs(listOfTeacherIds, day)
      listOfExternalIds = listOfIDs.filter(_.isExternalId).distinct
      listExternals <- generateExternalListFromIDs(listOfExternalIds, day)

    } yield (viva, listTeachers, listExternals)

  val res = generateADay.sample.getOrElse(LocalDate.now())

  property("Testing linkVivaWithResource") = forAll(generateVivaWithTeachersAndExternals(res)) { list =>
    println(list)
    true
  }
    
