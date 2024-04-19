package pj.domain

import pj.domain.ScheduleViva
import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, ODuration, Preference}

import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec

object Algorithm:
  def makeTheAlgorithmHappen(agenda: Agenda): Result[List[VivaResult]] =

    val groupedTeacherList = agenda.resources.teacher
      .flatMap { teacher =>
        teacher.availability.collect:
          case avail => (teacher.id, avail)
      }
      .groupBy(_._1)
      .view
      .mapValues(_.map(_._2))
      .toList

    val groupedExternalList = agenda.resources.external
      .flatMap(external => external.availability.map(avail => (external.id, avail)))
      .groupBy(_._1)
      .view
      .mapValues(_.map(_._2))
      .toList

    val groupedAvailabilitiesList = groupedTeacherList ++ groupedExternalList

    val vivas = agenda.vivas
    val duration = agenda.duration

    def CreateSchedule(vivas: Seq[Viva], teacherAvai: List[(ID, List[Availability])]): Seq[ScheduleViva] =
      vivas.map { viva =>
        ScheduleViva.from(
          president = RoleAvailabilities.from(viva.president, teacherAvai.filter(_._1 == viva.president.id)),
          advisor = RoleAvailabilities.from(viva.advisor, teacherAvai.filter(_._1 == viva.advisor.id)),
          supervisor = RoleAvailabilities.from(viva.supervisor, teacherAvai.filter(_._1 == viva.supervisor.id)),
          viva
        )
      }

    val scheduleVivaList = CreateSchedule(vivas, groupedAvailabilitiesList)

    def ExtractAvail(scheduleViva: ScheduleViva): Result[List[Availability]] =
      val presidentAval = scheduleViva.president.availabilities.flatMap(_._2)
      val advisorAval = scheduleViva.advisor.availabilities.flatMap(_._2)
      val supervisorAval = scheduleViva.supervisor.availabilities.flatMap(_._2)
      val results = findBestCombinedAvailability(presidentAval, advisorAval, supervisorAval, duration)
      results

    def findBestCombinedAvailability(presAvails: List[Availability], advAvails: List[Availability], supAvails: List[Availability], requiredDuration: ODuration): Result[List[Availability]] =
      def overlapThree(a1: List[Availability], a2: List[Availability], a3: List[Availability], requiredDuration: ODuration): List[Availability] =
        for {
          avail1 <- a1
          avail2 <- a2
          avail3 <- a3
          start = List(avail1.start, avail2.start, avail3.start).foldLeft(avail1.start)((acc, x) => if (x.isAfter(acc)) x else acc)
          end = List(avail1.end, avail2.end, avail3.end).foldLeft(avail1.end)((acc, x) => if (x.isBefore(acc)) x else acc)
          if start.isBefore(end) && Duration.between(start.toTemporal, end.toTemporal).compareTo(requiredDuration.toDuration) >= 0
        } yield Availability(start, end, Preference.add(avail1.preference, avail2.preference, avail3.preference))

      val totalOverlap = overlapThree(presAvails, advAvails, supAvails, requiredDuration)
      if (totalOverlap.isEmpty)
        Left(DomainError.Error("No valid overlapping availabilities found"))
      else
        Right(totalOverlap)

    def processSchedules(schedules: List[Result[List[Availability]]], requiredDuration: Duration): List[Availability] =
      @tailrec
      def helper(schedules: List[Result[List[Availability]]], booked: List[Availability], acc: List[Availability]): List[Availability] =
        schedules match
          case Nil => acc
          case Right(avails) :: tail =>
            avails.filter(a => durationMatches(requiredDuration, a.start.toLocalDateT, a.end.toLocalDateT) && noOverlaps(booked, a)) match
              case selected :: _ => helper(tail, selected :: booked, selected :: acc)
              case _ => helper(tail, booked, acc)
          case Left(_) :: tail => helper(tail, booked, acc)

      helper(schedules, List.empty, List.empty)

    def durationMatches(required: Duration, start: LocalDateTime, end: LocalDateTime): Boolean =
      Duration.between(start, end) == required

    def noOverlaps(booked: List[Availability], candidate: Availability): Boolean =
      booked.forall(booked => !(booked.start.isBefore(candidate.end) && booked.end.isAfter(candidate.start)))

    val result: Map[ScheduleViva, Result[List[Availability]]] = scheduleVivaList.map { viva =>
      viva -> ExtractAvail(viva)
    }.toMap

    //// president, advisor  1  required
    //// coadvisor, supervisor 0..n optionl

    val vivaResult: List[VivaResult] = scheduleVivaList.flatMap { viva =>
      val president = agenda.resources.teacher.find(t =>
        viva.president.id.toString.contains(t.id.toString))
      val supervisor = agenda.resources.external.find(t => viva.supervisor.id.toString.contains(t.id.toString))
      val advisor = agenda.resources.teacher.find(t => viva.advisor.id.toString.contains(t.id.toString))

      (president, supervisor, advisor) match
        case (Some(foundPres), Some(foundSuper), Some(foundAd)) =>
          val advisors: List[External] = List(foundSuper)
          val vivaSchedules = result.get(viva)

          println("////////////////////")
          println(vivaSchedules)
          println("////////////////////")

          vivaSchedules match
            case Some(n) =>
              val finalSelections = processSchedules(List(n), duration.toDuration).sortBy(_.start)
              finalSelections.headOption.map { selection =>

                Some(VivaResult.from(selection, foundPres, foundAd, advisors))
              }.getOrElse(None)
            case None =>
              None
        case _ =>
          println("Professor não encontrado")
          None
    }.toList

    println(vivaResult)
    //println("------------------------------------------------------------------")
    //println(result)
    //println("------------------------------------------------------------------")
    //println(finalSelections)
//Right(vivaResult)

    Right(vivaResult)

  