package pj.MS02.handleAlgorithm.ComplexGenerator

import org.scalacheck.Prop.{exists, forAll}
import org.scalacheck.{Gen, Prop, Properties}
import pj.MS02.handleAlgorithm.ComplexGenerator.PreVivaTest.generatePreVivaList
import pj.MS02.handleAlgorithm.ComplexGenerator.ResourceTest.{generateExternal, generateExternalListFromIDs, generateTeacherListFromIDs}
import pj.MS02.handleAlgorithm.ComplexGenerator.VivaTest.{generateViva, generateVivaList}
import pj.MS02.handleAlgorithm.ComplexGenerator.linkVivaWithResourceTest.generateAValidAgendaViva
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.OTimeTest.{generateADay, generateTime}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.PreferenceTest.generatePreference
import pj.domain.*
import pj.domain.Algorithm.{algorithm, preVivaToMap}
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.OTime
import pj.opaqueTypes.OTime.OTime

import java.time.LocalDate

object makeTheAlgorithmHappen
  extends Properties("Testing The Algorithm"):

  def generatePossibleSchedule: Gen[ScheduleOut] =
    for {
      previvaList <- generatePreVivaList
      preVivas = previvaList._1
      duration = previvaList._2
      availabilityMap = preVivaToMap(preVivas)
      scheduleOut = algorithm(preVivas, availabilityMap, duration)
      res <- scheduleOut match
        case Left(_) => Gen.fail
        case Right(validSchedule) => Gen.const(validSchedule)
    } yield res



  property("Putting the generated PreVivas in the algorithm") = forAll(generatePossibleSchedule) { res =>
    res.posVivas.foreach(x => println(x.start + " " + x.end))
    def toOTime(timeStr: String): OTime =
      OTime.createTime(timeStr) match
        case Right(value) => value

    def intervalsOverlap(start1: OTime, end1: OTime, start2: OTime, end2: OTime): Boolean =
      start1.isBefore(end2) && end1.isAfter(start2)

    res.posVivas.forall { viva =>
      val start1 = toOTime(viva.start)
      val end1 = toOTime(viva.end)
      res.posVivas.filterNot(_ == viva).forall { other =>
        val start2 = toOTime(other.start)
        val end2 = toOTime(other.end)
        !intervalsOverlap(start1, end1, start2, end2)
      }
    }
  }