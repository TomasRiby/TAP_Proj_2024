package pj.MS02.handleAlgorithm.ComplexGenerator

import org.scalacheck.Prop.{exists, forAll}
import org.scalacheck.{Gen, Prop, Properties}
import ResourceTest.{generateExternal, generateExternalListFromIDs, generateTeacherListFromIDs}
import VivaTest.{generateViva, generateVivaList}
import pj.MS02.handleAlgorithm.ComplexGenerator.AvailabilityGenerators.{avail, generateAvailabilityFromDayWithDuration, generateAvailabilityListForADayWithDuration}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.ODurationTest.generateDuration
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.OTimeTest.{generateADay, generateTime}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.PreferenceTest.generatePreference
import pj.domain.*
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.{ODuration, OTime, Preference}
import pj.opaqueTypes.OTime.OTime

import scala.jdk.CollectionConverters.*
import java.time.LocalDate

object linkVivaWithResourceTest
  extends Properties("Testing linkVivaWithResource"):
  def generateVivaWithTeachersAndExternals(day: LocalDate, duration: ODuration, availability: Availability): Gen[(Viva, List[Teacher], List[External])] =
    for {
      vivaGenerated <- generateViva
      viva = vivaGenerated._1
      listOfIDs = vivaGenerated._2
      listOfTeacherIds = listOfIDs.filter(_.isTeacherId).distinct
      listTeachers <- generateTeacherListFromIDs(listOfTeacherIds, day, availability)
      listOfExternalIds = listOfIDs.filter(_.isExternalId).distinct
      listExternals <- generateExternalListFromIDs(listOfExternalIds, day, availability)
    } yield (viva, listTeachers, listExternals)

  def generateAValidAgendaViva: Gen[(List[Viva], List[Teacher], List[External], ODuration, List[Availability])] =
    for {
      day <- generateADay
      size <- Gen.choose(1, 4)
      duration <- generateDuration
      availabilityList <- generateAvailabilityListForADayWithDuration(day, duration, size)
      vivaTuples <- Gen.sequence[List[(Viva, List[Teacher], List[External])], (Viva, List[Teacher], List[External])](availabilityList.map(avail => generateVivaWithTeachersAndExternals(day, duration, avail)))
      vivas = vivaTuples.map(_._1)
      teacherList = vivaTuples.flatMap(_._2).distinct
      externals = vivaTuples.flatMap(_._3).distinct
    } yield (vivas, teacherList, externals, duration, availabilityList)

  val day = generateADay.sample.getOrElse(LocalDate.now())
  val time = generateDuration.sample.getOrElse(ODuration.from())

  val avail = for {
    start <- OTime.createTime("2024-06-11T09:30:00")
    end <- OTime.createTime("2024-06-11T10:30:00")
    preference <- Preference.createPreference(5)
    avail <- Availability.fromCheck(start, end, preference)
  } yield avail

  avail match
    case Right(availx) => property("All teachers and externals have the given availability") = forAll(generateVivaWithTeachersAndExternals(day, time, availx)) { case (viva, teachers, externals) =>
      val allTeachersHaveAvailability = teachers.forall(_.availability.contains(availx))
      val allExternalsHaveAvailability = externals.forall(_.availability.contains(availx))
      allTeachersHaveAvailability && allExternalsHaveAvailability

    }

  property("Each external and teacher has at least one availability from the list") = forAll(generateAValidAgendaViva) { case (vivas, teachers, externals, duration, availabilityList) =>
    val allTeachersHaveAtLeastOneAvailability = teachers.forall { teacher =>
      teacher.availability.exists(availabilityList.contains)
    }
    val allExternalsHaveAtLeastOneAvailability = externals.forall { external =>
      external.availability.exists(availabilityList.contains)
    }
    allTeachersHaveAtLeastOneAvailability && allExternalsHaveAtLeastOneAvailability
  }

    
