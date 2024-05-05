package pj.domain

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

  def president: Teacher

  def advisor: Teacher

  def supervisors: List[External]

  def coadvisors: List[Resource]

final case class VivaNotScheduled(student: Name, title: Title,
                                  president: Teacher, advisor: Teacher,
                                  supervisors: List[External], coadvisors: List[Resource]) extends Viva

object VivaNotScheduled:

  private def isValidPresident(president: Teacher, teachers: List[Teacher], title: String): Result[Unit] =
    if (teachers.contains(president)) Right(())
    else Left(DomainError.InvalidPresidentId(s"The president $president is invalid for viva $title"))

  private def isValidAdvisor(advisor: Teacher, teachers: List[Teacher], title: String): Result[Unit] =
    if (teachers.contains(advisor)) Right(())
    else Left(DomainError.InvalidAdvisorId(s"The advisor $advisor is invalid for viva $title"))

  private def isValidResource(resource: Resource, resources: List[Resource]): Boolean =
    resources.contains(resource)

  private def isValidResources(resources: List[Resource], validList: List[Resource], error: DomainError): Result[Unit] =
    if (resources.forall(resource => isValidResource(resource, validList))) Right(())
    else Left(error)

  private def moreThanOneRole(ids: List[Resource], title: String): Result[Unit] =
    if (ids.distinct.size.eq(ids.size)) Right(())
    else Left(DomainError.MoreThanOneRole(s"There are resources with more than one role for viva $title"))

  def from(student: Name, title: Title, president: Teacher, advisor: Teacher,
           supervisors: List[External], coadvisors: List[Resource], teachers: List[Teacher], externals: List[External]): Result[VivaNotScheduled] =
    for
      _ <- isValidPresident(president, teachers, title.toString)
      _ <- isValidAdvisor(advisor, teachers, title.toString)
      _ <- isValidResources(coadvisors, teachers ++ externals, DomainError.InvalidCoadvisorId(s"The viva $title contains invalid coadvisors ids"))
      _ <- isValidResources(supervisors, externals, DomainError.InvalidSupervisorId(s"The viva $title contains invalid supervisors ids"))
      _ <- moreThanOneRole(president :: advisor :: supervisors ++ coadvisors, title.toString)
    yield VivaNotScheduled(student, title, president, advisor, supervisors, coadvisors)

  def getResource(viva: VivaNotScheduled): Result[List[Resource]] =
    Right(viva.president :: viva.advisor :: viva.coadvisors ++ viva.supervisors)

  def getVivaTeachers(resources: List[Resource]): Result[List[Teacher]] =
    Right(resources.collect { case t: Teacher => t })

  def getVivaExternals(resources: List[Resource]): Result[List[External]] =
    Right(resources.collect { case e: External => e })

final case class VivaScheduled(student: Name, title: Title, start: DateTime, end: DateTime,
                               preference: Int, president: Teacher, advisor: Teacher, coadvisors: List[Resource],
                               supervisors: List[External]) extends Viva

object VivaScheduled:
  def from(student: Name, title: Title, start: DateTime, end: DateTime,
           preference: Int, president: Teacher, advisor: Teacher,
           coadvisors: List[Resource], supervisor: List[External]): Result[VivaScheduled] =
    Right(VivaScheduled(student, title, start, end, preference, president, advisor, coadvisors, supervisor))