package pj.io

import pj.domain.PreViva.{getVivaExternals, getVivaTeachers}
import pj.domain.*
import pj.domain.Agenda
import pj.io.AvailabilityIO.findEarliestCommonAvailability
import pj.io.ResourceIO.{calculatePreference, get_Teacher, syncExternals, syncTeachers}
import pj.xml.XML

import scala.xml.Node
import pj.opaqueTypes.{ID, Name, OTime, Preference}

object VivaIO:

  def syncVivaNotPlanned(vivaNotScheduled: PreViva, teachers: List[Teacher], externals: List[External]): Result[PreViva] =
    val updatedPresident = fetchUpdatedTeacher(vivaNotScheduled.president, teachers)
    val updatedAdvisor = fetchUpdatedTeacher(vivaNotScheduled.advisor, teachers)
    val updatedCoadvisors = fetchUpdatedCoadvisors(vivaNotScheduled.coadvisors, teachers, externals)
    val updatedSupervisors = fetchUpdatedSupervisors(vivaNotScheduled.supervisors, externals)

    for {
      president <- updatedPresident
      advisor <- updatedAdvisor
      coadvisors <- updatedCoadvisors
      supervisors <- updatedSupervisors
      viva <- PreViva.from(vivaNotScheduled.student, vivaNotScheduled.title, president, advisor, supervisors, coadvisors, teachers, externals)
    } yield viva

  private def fetchUpdatedTeacher(teacher: Teacher, teachers: List[Teacher]): Result[Teacher] =
    teachers.find(_.id == teacher.id).toRight(DomainError.ImpossibleSchedule)

  private def fetchUpdatedCoadvisors(coadvisors: List[Teacher | External],teachers: List[Teacher],externals: List[External]): Result[List[Teacher | External]] =
    val coadvisorIds = coadvisors.collect { case t: Teacher => t.id case e: External => e.id }
    val updatedTeachers = teachers.filter(teacher => coadvisorIds.contains(teacher.id))
    val updatedExternals = externals.filter(external => coadvisorIds.contains(external.id))
    Right(updatedTeachers ++ updatedExternals)

  private def fetchUpdatedSupervisors(previous: List[External], externals: List[External]): Result[List[External]] =
    Right(externals.filter(external => previous.map(_.id).contains(external.id)))

  def scheduleVivas(agenda: Agenda): Result[List[PosViva]] =
    val initial: Result[(Vector[PosViva], List[Teacher], List[External])] = Right((Vector.empty[PosViva], agenda.teachers, agenda.externals))

    val scheduledVivas = agenda.vivas.foldLeft(initial) { (res, viva) =>
      for {
        (vivasScheduled, teachers, externals) <- res
        updatedViva <- VivaIO.syncVivaNotPlanned(viva, teachers, externals)
        vivaResources <- PreViva.getResource(updatedViva)
        timeSlot <- findEarliestCommonAvailability(vivaResources, agenda.duration)
        preference <- calculatePreference(timeSlot, vivaResources)
        vivaTeachers <- getVivaTeachers(vivaResources)
        vivaExternals <- getVivaExternals(vivaResources)
        updatedTeachers <- syncTeachers(timeSlot, teachers, vivaTeachers)
        updatedExternals <- syncExternals(timeSlot, externals, vivaExternals)
        vivaScheduled <- PosViva.from(viva.student, viva.title, timeSlot.start, timeSlot.end, preference, viva.president, viva.advisor, viva.coadvisors, viva.supervisors)
      } yield (vivasScheduled :+ vivaScheduled, updatedTeachers, updatedExternals)
    }

    scheduledVivas.map(_._1.toList).map(_.sortBy(_.start.toLocalDateT))

  def loadViva(vivaNode: Node, teachers: List[Teacher], externals: List[External]): Result[PreViva] =
    for {
      studentString <- XML.fromAttribute(vivaNode, "student")
      student <- Name.createName(studentString)
      titleString <- XML.fromAttribute(vivaNode, "title")
      title <- Title.from(titleString)

      president <- getTeacherFromNode(vivaNode, "president", teachers)
      advisor <- getTeacherFromNode(vivaNode, "advisor", teachers)

      _ <- ResourceIO.moreThanOneRoleValidation(advisor.id.toString, List(president.id.toString))

      coadvisors <- ResourceIO.parseCoadvisorsNode(vivaNode \ "coadvisor", teachers, externals, List(president.id.toString, advisor.id.toString))
      coadvisorsIds = coadvisors.map:
        case teacher: Teacher => teacher.id.IDtoString
        case external: External => external.id.IDtoString

      supervisors <- ResourceIO.parseSupervisorsNode(vivaNode \ "supervisor", externals, coadvisorsIds)

      viva <- PreViva.from(student, title, president, advisor, supervisors, coadvisors, teachers, externals)
    } yield viva

  private def getTeacherFromNode(vivaNode: Node, role: String, teachers: List[Teacher]): Result[Teacher] =
    for {
      roleNode <- XML.fromNode(vivaNode, role)
      roleId <- XML.fromAttribute(roleNode, "id")
      teacher <- get_Teacher(roleId, teachers)
    } yield teacher
