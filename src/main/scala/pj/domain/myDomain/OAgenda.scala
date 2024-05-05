package pj.domain.myDomain

import pj.domain.{Agenda, DomainError, OExternal, OTeacher, Result, Time, VivaNotScheduled, VivaScheduled}
import pj.opaqueTypes.OTime.OTime

final case class OAgenda(vivas: List[VivaNotScheduled], teachers: List[OTeacher], externals: List[OExternal], duration: Time)

object OAgenda:

  private def emptyList[T](list: List[T], error: DomainError): Result[Unit] =
    if (list.isEmpty) Left(error)
    else Right(())

  private def duplicateValues[T](list: List[T], property: T => Any, error: DomainError): Result[Unit] =
    if (list.distinctBy(property).size.ne(list.size)) Left(error)
    else Right(())

  def from(vivas: List[VivaNotScheduled], teachers: List[OTeacher], externals: List[OExternal], duration: Time): Result[OAgenda] =
    for
      _ <- emptyList(vivas, DomainError.NoVivasInAgenda)
      _ <- emptyList(vivas, DomainError.NoTeachersInAgenda)
      _ <- duplicateValues(vivas, viva => viva.title, DomainError.DuplicateVivasInAgenda)
      _ <- duplicateValues(vivas, viva => viva.student, DomainError.StudentWithMultipleVivas)
    yield OAgenda(vivas, teachers, externals, duration)


final case class AgendaOut(vivas: List[VivaScheduled], preference: Int)

object AgendaOut:
  def from(vivas: List[VivaScheduled]): Result[AgendaOut] =
    val preference = vivas.map(viva => viva.preference).sum
    Right(AgendaOut(vivas, preference))