package pj.domain

import pj.opaqueTypes.OTime.OTime

import scala.annotation.targetName

opaque type Title = String

object Title:

  private def isValidTitle(title: String): Result[Unit] =
    if (title.isBlank) Left(DomainError.InvalidVivaTitle)
    else Right(())

  def from(title: String): Result[Title] =
    for {
      _ <- isValidTitle(title)
    } yield title

  extension (string: Title)
    @targetName("Title.TitleToString")
    def TitleToString: String = string

sealed trait Viva:

  def president: OTeacher

  def advisor: OTeacher

  def supervisors: List[OExternal]

  def coadvisors: List[OTeacher | OExternal]

final case class VivaNotScheduled(student: Name, title: Title,
                                  president: OTeacher, advisor: OTeacher,
                                  supervisors: List[OExternal], coadvisors: List[OTeacher | OExternal]) extends Viva

object VivaNotScheduled:

  private def isValidPresident(president: OTeacher, teachers: List[OTeacher], title: String): Result[Unit] =
    if (teachers.contains(president)) Right(())
    else Left(DomainError.InvalidPresidentId(s"The president $president is invalid for viva $title"))

  private def isValidAdvisor(advisor: OTeacher, teachers: List[OTeacher], title: String): Result[Unit] =
    if (teachers.contains(advisor)) Right(())
    else Left(DomainError.InvalidAdvisorId(s"The advisor $advisor is invalid for viva $title"))

  private def isValidResource(resource: OTeacher | OExternal, resources: List[OTeacher | OExternal]): Boolean =
    resources.contains(resource)

  private def isValidResources(resources: List[OTeacher | OExternal], validList: List[OTeacher | OExternal], error: DomainError): Result[Unit] =
    if (resources.forall(resource => isValidResource(resource, validList))) Right(())
    else Left(error)

  private def moreThanOneRole(ids: List[OTeacher | OExternal], title: String): Result[Unit] =
    if (ids.distinct.size.eq(ids.size)) Right(())
    else Left(DomainError.MoreThanOneRole(s"There are resources with more than one role for viva $title"))

  def from(student: Name, title: Title, president: OTeacher, advisor: OTeacher,
           supervisors: List[OExternal], coadvisors: List[OTeacher | OExternal], teachers: List[OTeacher], externals: List[OExternal]): Result[VivaNotScheduled] =
    for
      _ <- isValidPresident(president, teachers, title.toString)
      _ <- isValidAdvisor(advisor, teachers, title.toString)
      _ <- isValidResources(coadvisors, teachers ++ externals, DomainError.InvalidCoadvisorId(s"The viva $title contains invalid coadvisors ids"))
      _ <- isValidResources(supervisors, externals, DomainError.InvalidSupervisorId(s"The viva $title contains invalid supervisors ids"))
      _ <- moreThanOneRole(president :: advisor :: supervisors ++ coadvisors, title.toString)
    yield VivaNotScheduled(student, title, president, advisor, supervisors, coadvisors)

  def getResource(viva: VivaNotScheduled): Result[List[OTeacher | OExternal]] =
    Right(viva.president :: viva.advisor :: viva.coadvisors ++ viva.supervisors)

  def getVivaTeachers(resources: List[OTeacher | OExternal]): Result[List[OTeacher]] =
    Right(resources.collect { case t: OTeacher => t })

  def getVivaExternals(resources: List[OTeacher | OExternal]): Result[List[OExternal]] =
    Right(resources.collect { case e: OExternal => e })

final case class VivaScheduled(student: Name, title: Title, start: OTime, end: OTime,
                               preference: Int, president: OTeacher, advisor: OTeacher, coadvisors: List[OTeacher | OExternal],
                               supervisors: List[OExternal]) extends Viva

object VivaScheduled:
  def from(student: Name, title: Title, start: OTime, end: OTime,
           preference: Int, president: OTeacher, advisor: OTeacher,
           coadvisors: List[OTeacher | OExternal], supervisor: List[OExternal]): Result[VivaScheduled] =
    Right(VivaScheduled(student, title, start, end, preference, president, advisor, coadvisors, supervisor))