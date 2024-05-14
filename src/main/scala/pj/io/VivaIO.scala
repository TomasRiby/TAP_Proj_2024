package pj.io

import pj.domain.VivaNotScheduled.{getVivaExternals, getVivaTeachers}
import pj.domain.*
import pj.domain.Agenda
import pj.io.AvailabilityIO.findEarliestCommonOAvailability
import pj.io.ResourceIO.{calculatePreference, updateExternals, updateTeachers, getTeacher}
import pj.xml.XML
import scala.xml.Node
import pj.opaqueTypes.{ID, Name, Preference, OTime}

object VivaIO:

  def updateVivaNotScheduled(vivaNotScheduled: VivaNotScheduled, teachers: List[Teacher], externals: List[External]): Result[VivaNotScheduled] =
    for
      president <- retrieveUpdatedTeacher(vivaNotScheduled.president, teachers)
      advisor <- retrieveUpdatedTeacher(vivaNotScheduled.advisor, teachers)
      coadvisors <- retrieveUpdatedCoadvisors(vivaNotScheduled.coadvisors, teachers, externals)
      supervisors <- retrieveUpdatedSupervisors(vivaNotScheduled.supervisors, externals)
      viva <- VivaNotScheduled.from(vivaNotScheduled.student, vivaNotScheduled.title, president, advisor, supervisors, coadvisors, teachers, externals)
    yield viva

  private def retrieveUpdatedTeacher(teacher: Teacher, teachers: List[Teacher]): Result[Teacher] =
    teachers.find(t => t.id == teacher.id) match
      case Some(value) => Right(value)
      case None => Left(DomainError.ImpossibleSchedule)

  private def retrieveUpdatedCoadvisors(coadvisors: List[Teacher | External], teachers: List[Teacher], externals: List[External]): Result[List[Teacher | External]] =
    val updatedTeachers = teachers.filter(teacher => coadvisors.collect { case t: Teacher => t.id }.contains(teacher.id))
    val updatedExternals = externals.filter(external => coadvisors.collect { case e: External => e.id }.contains(external.id))
    Right(updatedTeachers ++ updatedExternals)

  private def retrieveUpdatedSupervisors(previous: List[External], externals: List[External]): Result[List[External]] =
    Right(externals.filter(external => previous.map(_.id).contains(external.id)))

  def scheduleVivas(agenda: Agenda): Result[List[VivaScheduled]] =
    agenda.vivas.foldLeft[Result[(Vector[VivaScheduled], List[Teacher], List[External])]](Right((Vector.empty[VivaScheduled], agenda.teachers, agenda.externals))) { case (res, viva) =>
      for {
        valuesTuple <- res
        updatedViva <- updateVivaNotScheduled(viva, valuesTuple._2, valuesTuple._3)
        vivaResources <- VivaNotScheduled.getResource(updatedViva)
        timeSlot <- findEarliestCommonOAvailability(vivaResources, agenda.duration)
        preference <- calculatePreference(timeSlot, vivaResources)
        vivaTeachers <- getVivaTeachers(vivaResources)
        vivaExternals <- getVivaExternals(vivaResources)
        updatedTeachers <- updateTeachers(timeSlot, valuesTuple._2, vivaTeachers)
        updatedExternals <- updateExternals(timeSlot, valuesTuple._3, vivaExternals)
        vivaScheduled <- VivaScheduled.from(viva.student, viva.title, timeSlot.start, timeSlot.end, preference, viva.president, viva.advisor, viva.coadvisors, viva.supervisors)
      } yield (valuesTuple._1 :+ vivaScheduled, updatedTeachers, updatedExternals)
    }.map(_._1.toList).map(_.sortBy(_.start.toLocalDateT))

  def parseViva(vivaNode: Node, teachers: List[Teacher], externals: List[External]): Result[VivaNotScheduled] =
    val resourcesIds = List.empty[String]
    for
      studentString <- XML.fromAttribute(vivaNode, "student")
      student <- Name.createName(studentString)
  
      titleString <- XML.fromAttribute(vivaNode, "title")
      title <- Title.from(titleString)
  
      presidentNode <- XML.fromNode(vivaNode, "president")
      presidentId <- XML.fromAttribute(presidentNode, "id")
      president <- getTeacher(presidentId, teachers)
  
      advisorNode <- XML.fromNode(vivaNode, "advisor")
      advisorId <- XML.fromAttribute(advisorNode, "id")
      advisor <- getTeacher(advisorId, teachers)
      _ <- ResourceIO.moreThanOneRoleValidation(advisorId, presidentId :: resourcesIds)
  
      coadvisors <- ResourceIO.parseCoadvisorsNode(vivaNode \ "coadvisor", teachers, externals, presidentId :: advisorId :: resourcesIds)
      coadvisorsIds = coadvisors.map:
        case teacher: Teacher => teacher.id.IDtoString
        case external: External => external.id.IDtoString
  
      supervisors: List[External] <- ResourceIO.parseSupervisorsNode(vivaNode \ "supervisor", externals, coadvisorsIds ::: resourcesIds)
  
      viva <- VivaNotScheduled.from(student, title, president, advisor, supervisors, coadvisors, teachers, externals)
    yield viva