package pj.MS02.handleAlgorithm

import org.scalacheck.Prop.{exists, forAll}
import org.scalacheck.{Gen, Prop, Properties}
import pj.MS02.handleAlgorithm.ComplexGenerator.ResourceTest.{generateExternal, generateExternalListFromIDs, generateTeacherListFromIDs}
import pj.MS02.handleAlgorithm.ComplexGenerator.VivaTest.{generateViva, generateVivaList}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.OTimeTest.{generateADay, generateTime}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.PreferenceTest.generatePreference
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

  property("all generated teacher and external IDs should be present in the list of IDs from Viva") =
    forAll(generateVivaWithTeachersAndExternals(res)) { case (viva, teachers, externals) =>
      val teacherIds = teachers.map(_.id)
      val externalIds = externals.map(_.id)
      val listOfVivaIDs: List[ID] =
        val pId = List(viva.president.id)
        val aId = List(viva.advisor.id)
        val sIds = viva.supervisor.map(_.id)
        val cIds = viva.coAdvisor.map(_.id)

        pId ++ aId ++ sIds ++ cIds
      teacherIds.forall(id => listOfVivaIDs.contains(id)) &&
        externalIds.forall(id => listOfVivaIDs.contains(id))
    }

  property("the lengths of generated lists should match the number of distinct IDs") =
    forAll(generateVivaWithTeachersAndExternals(res)) { case (viva, teachers, externals) =>
      val teacherIds = teachers.map(_.id)
      val externalIds = externals.map(_.id)
      teacherIds.distinct.lengthIs == teacherIds.length &&
        externalIds.distinct.lengthIs == externalIds.length
    }

    
