package pj.MS02.handleAlgorithm.ComplexGenerator

import org.scalacheck.Prop.{exists, forAll}
import org.scalacheck.{Gen, Prop, Properties}
import ResourceTest.{generateExternal, generateExternalListFromIDs, generateTeacherListFromIDs}
import VivaTest.{generateViva, generateVivaList}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.ODurationTest.generateDuration
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.OTimeTest.{generateADay, generateTime}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.PreferenceTest.generatePreference
import pj.domain.*
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.ODuration.ODuration
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

  def generateAValidAgendaViva: Gen[(List[Viva], List[Teacher], List[External], ODuration)] =
    for {
      day <- generateADay
      vivaTuples <- Gen.listOfN(10, generateVivaWithTeachersAndExternals(day))
      vivas = vivaTuples.map(_._1)
      teacherList = vivaTuples.flatMap(_._2).distinct
      externals = vivaTuples.flatMap(_._3).distinct
      duration <- generateDuration
    } yield (vivas, teacherList, externals, duration)

  val res = generateADay.sample.getOrElse(LocalDate.now())

  property("Testing linkVivaWithResource") = forAll(generateVivaWithTeachersAndExternals(res)) { list =>
    true
  }

  property("Testing linkVivaWithResource") = forAll(generateAValidAgendaViva) { list =>
    true
  }
    
