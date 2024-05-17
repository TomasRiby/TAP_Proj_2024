package pj.domain


import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference.Preference

import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec


object AlgorithmV2:
  def makeTheAlgorithmHappen(agenda: Agenda): Result[Unit] =
    val teacherList = agenda.resources.teacher
    val externalList = agenda.resources.external
    val duration = agenda.duration

    val preVivaList = agenda.vivas.map(PreViva.linkVivaWithResource(_, teacherList, externalList))

    preVivaList.headOption match
      case Some(value) => PreVivaToMap(value)
      case None => println("No vivas found")

    def PreVivaToMap(viva: PreViva):  Map[President | Advisor | Supervisor | CoAdvisor, List[Availability]] =
      val roles = viva.roleLinkedWithResourceList.collect(_.role)
      val listAvailabilities = viva.roleLinkedWithResourceList.collect(_.listAvailability)
      roles.zip(listAvailabilities).toMap

    val res = preVivaList.map(PreVivaToMap)
    res.foreach(println)



    //    def findCommonAvailability(
    //                                presAvail: List[Availability],
    //                                advAvail: List[Availability],
    //                                supAvail: List[Availability],
    //                                coAdvAvail: List[Availability],
    //                                duration: Duration
    //                              ): List[Availability] =
    //      val allAvailabilities = List(presAvail, advAvail, supAvail, coAdvAvail).flatten
    //
    //      @tailrec
    //      def intersectAvailabilities(remaining: List[Availability], acc: List[Availability]): List[Availability] =
    //        remaining match
    //          case Nil => acc
    //          case head :: tail =>
    //            val common = acc.flatMap(a => if Availability.intersects(a, head) then Some(a) else None)
    //            intersectAvailabilities(tail, common)
    //
    //      val initialAvailability = allAvailabilities.headOption.toList
    //      intersectAvailabilities(allAvailabilities.drop(1), initialAvailability)

    Right(())



