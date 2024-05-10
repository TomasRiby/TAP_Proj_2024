package pj.domain

import pj.domain.{DomainError, External, Teacher, Result, VivaNotScheduled, VivaScheduled}
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.OTime.OTime

final case class Agenda(vivas: List[VivaNotScheduled], teachers: List[Teacher], externals: List[External], duration: ODuration)

object Agenda:

  private def emptyList[T](list: List[T], error: DomainError): Result[Unit] =
    if (list.isEmpty) Left(error)
    else Right(())

  private def duplicateValues[T](list: List[T], property: T => Any, error: DomainError): Result[Unit] =
    if (list.distinctBy(property).size.ne(list.size)) Left(error)
    else Right(())

  def from(vivas: List[VivaNotScheduled], teachers: List[Teacher], externals: List[External], duration: ODuration): Result[Agenda] =
    for
      _ <- emptyList(vivas, DomainError.NoVivasInAgenda)
      _ <- emptyList(vivas, DomainError.NoTeachersInAgenda)
      _ <- duplicateValues(vivas, viva => viva.title, DomainError.DuplicateVivasInAgenda)
      _ <- duplicateValues(vivas, viva => viva.student, DomainError.StudentWithMultipleVivas)
    yield Agenda(vivas, teachers, externals, duration)


final case class AgendaOut(vivas: List[VivaScheduled], preference: Int)

object AgendaOut:
  def from(vivas: List[VivaScheduled]): Result[AgendaOut] =
    val preference = vivas.map(viva => viva.preference).sum
    Right(AgendaOut(vivas, preference))