package pj.domain

import pj.opaqueTypes.Name.Name
import pj.opaqueTypes.OTime.OTime

import scala.annotation.targetName

opaque type Title = String

object Title:

  private def isValidTitle(title: String): Result[Unit] =
    if (title.isBlank) Left(DomainError.VIVA_INVALID_TITLE)
    else Right(())

  def from(title: String): Result[Title] =
    for {
      _ <- isValidTitle(title)
    } yield title

  extension (string: Title)
    @targetName("Title.TitleToString")
    def TitleToString: String = string

sealed trait Viva:

  def president: Teacher

  def advisor: Teacher

  def supervisors: List[External]

  def coadvisors: List[Teacher | External]

final case class VivaNotScheduled(student: Name, title: Title,
                                  president: Teacher, advisor: Teacher,
                                  supervisors: List[External], coadvisors: List[Teacher | External]) extends Viva

object VivaNotScheduled:

  private def isValidPresident(president: Teacher, teachers: List[Teacher], title: String): Result[Unit] =
    if (teachers.contains(president)) Right(())
    else Left(DomainError.VIVA_INVALID_PRESIDENT(s"The president $president is invalid for viva $title"))

  private def isValidAdvisor(advisor: Teacher, teachers: List[Teacher], title: String): Result[Unit] =
    if (teachers.contains(advisor)) Right(())
    else Left(DomainError.VIVA_INVALID_ADVISOR(s"The advisor $advisor is invalid for viva $title"))

  private def isValidResource(resource: Teacher | External, resources: List[Teacher | External]): Boolean =
    resources.contains(resource)

  private def isValidResources(resources: List[Teacher | External], validList: List[Teacher | External], error: DomainError): Result[Unit] =
    if (resources.forall(resource => isValidResource(resource, validList))) Right(())
    else Left(error)

  private def moreThanOneRole(ids: List[Teacher | External], title: String): Result[Unit] =
    if (ids.distinct.size.eq(ids.size)) Right(())
    else Left(DomainError.VIVA_MULTIPLE_ROLES(s"There are resources with more than one role for viva $title"))

  def from(student: Name, title: Title, president: Teacher, advisor: Teacher,
           supervisors: List[External], coadvisors: List[Teacher | External], teachers: List[Teacher], externals: List[External]): Result[VivaNotScheduled] =
    for
      _ <- isValidPresident(president, teachers, title.toString)
      _ <- isValidAdvisor(advisor, teachers, title.toString)
      _ <- isValidResources(coadvisors, teachers ++ externals, DomainError.VIVA_INVALID_COADVISOR(s"The viva $title contains invalid coadvisors ids"))
      _ <- isValidResources(supervisors, externals, DomainError.VIVA_INVALID_SUPERVISOR(s"The viva $title contains invalid supervisors ids"))
      _ <- moreThanOneRole(president :: advisor :: supervisors ++ coadvisors, title.toString)
    yield VivaNotScheduled(student, title, president, advisor, supervisors, coadvisors)

  def getResource(viva: VivaNotScheduled): Result[List[Teacher | External]] =
    Right(viva.president :: viva.advisor :: viva.coadvisors ++ viva.supervisors)

  def getVivaTeachers(resources: List[Teacher | External]): Result[List[Teacher]] =
    Right(resources.collect { case t: Teacher => t })

  def getVivaExternals(resources: List[Teacher | External]): Result[List[External]] =
    Right(resources.collect { case e: External => e })

final case class VivaScheduled(student: Name, title: Title, start: OTime, end: OTime,
                               preference: Int, president: Teacher, advisor: Teacher, coadvisors: List[Teacher | External],
                               supervisors: List[External]) extends Viva

object VivaScheduled:
  def from(student: Name, title: Title, start: OTime, end: OTime,
           preference: Int, president: Teacher, advisor: Teacher,
           coadvisors: List[Teacher | External], supervisor: List[External]): Result[VivaScheduled] =
    Right(VivaScheduled(student, title, start, end, preference, president, advisor, coadvisors, supervisor))