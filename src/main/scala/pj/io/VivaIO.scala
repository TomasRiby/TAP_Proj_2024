package pj.io

import pj.domain.VivaNotScheduled.{getVivaExternals, getVivaTeachers}
import pj.domain.*
import pj.io.AvailabilityIO.findEarliestCommonAvailability
import pj.io.ResourceIO.{calculatePreference, updateExternals, updateTeachers}

object VivaIO:

  def updateVivaNotScheduled(vivaNotScheduled: VivaNotScheduled, teachers: List[Teacher], externals: List[External]): Result[VivaNotScheduled] =
    for
      president <- retrieveUpdatedTeacher(vivaNotScheduled.president, teachers)
      advisor <- retrieveUpdatedTeacher(vivaNotScheduled.advisor, teachers)
      coadvisors <- retrieveUpdatedCoadvisors(vivaNotScheduled.coadvisors, teachers, externals)
      supervisors <- retrieveUpdatedSupervisors(vivaNotScheduled.supervisors, externals)
      viva <- VivaNotScheduled.from(vivaNotScheduled.student, vivaNotScheduled.title, president, advisor, supervisors, coadvisors, teachers, externals)
    yield viva

  //TODO MAKE A GENERIC THING FOR THIS YOU PIECE OF SHIT
  private def retrieveUpdatedTeacher(teacher: Teacher, teachers: List[Teacher]): Result[Teacher] =
    teachers.find(t => t.id == teacher.id) match
      case Some(value) => Right(value)
      case None => Left(DomainError.ImpossibleSchedule)

  private def retrieveUpdatedCoadvisors(coadvisors: List[Resource], teachers: List[Teacher], externals: List[External]): Result[List[Resource]] =
    val updatedTeachers = teachers.filter(teacher => coadvisors.collect { case t: Teacher => t.id }.contains(teacher.id))
    val updatedExternals = externals.filter(external => coadvisors.collect { case e: External => e.id }.contains(external.id))
    Right(updatedTeachers ++ updatedExternals)

  private def retrieveUpdatedSupervisors(previous: List[External], externals: List[External]): Result[List[External]] =
    Right(externals.filter(external => previous.map(_.id).contains(external.id)))

  def scheduleVivas(agenda: Agenda): Result[List[VivaScheduled]] =
    agenda.vivas.foldLeft[Result[(Vector[VivaScheduled], List[Teacher], List[External])]](Right((Vector.empty[VivaScheduled], agenda.teachers, agenda.externals))) { case (res, viva) =>
      for {
        valuesTuple <- res

        // Update the viva
        updatedViva <- updateVivaNotScheduled(viva, valuesTuple._2, valuesTuple._3)

        // Get the list of resources in the viva and calculate earliest timeslot
        vivaResources <- VivaNotScheduled.getResource(updatedViva)
        timeSlot <- findEarliestCommonAvailability(vivaResources, agenda.duration)

        // With the list of resources calculate the total preference
        preference <- calculatePreference(timeSlot, vivaResources)

        // Update the teachers and externals
        vivaTeachers <- getVivaTeachers(vivaResources)
        vivaExternals <- getVivaExternals(vivaResources)
        updatedTeachers <- updateTeachers(timeSlot, valuesTuple._2, vivaTeachers)
        updatedExternals <- updateExternals(timeSlot, valuesTuple._3, vivaExternals)

        vivaScheduled <- VivaScheduled.from(viva.student, viva.title, timeSlot.start, timeSlot.end, preference, viva.president, viva.advisor, viva.coadvisors, viva.supervisors)
      } yield (valuesTuple._1 :+ vivaScheduled, updatedTeachers, updatedExternals)
    }.map(_._1.toList).map(_.sortBy(_.start.to))