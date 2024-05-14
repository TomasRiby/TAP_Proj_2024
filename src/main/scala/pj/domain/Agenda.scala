package pj.domain

import pj.domain.{DomainError, External, Teacher, Result, VivaNotScheduled, VivaScheduled}
import pj.opaqueTypes.ODuration.ODuration

final case class Agenda(vivas: List[VivaNotScheduled], teachers: List[Teacher], externals: List[External], duration: ODuration)

object Agenda:

  private def validateNonEmpty[T](list: List[T], error: DomainError): Result[Unit] =
    Either.cond(list.nonEmpty, (), error)

  private def validateNoDuplicates[T](list: List[T], property: T => Any, error: DomainError): Result[Unit] =
    Either.cond(list.distinctBy(property).sizeIs == list.sizeIs, (), error)

  def from(vivas: List[VivaNotScheduled], teachers: List[Teacher], externals: List[External], duration: ODuration): Result[Agenda] =
    for {
      _ <- validateNonEmpty(vivas, DomainError.AGENDA_NO_VIVAS)
      _ <- validateNonEmpty(teachers, DomainError.AGENDA_NO_TEACHERS)
      _ <- validateNoDuplicates(vivas, _.title, DomainError.AGENDA_DUPLICATED_VIVAS)
      _ <- validateNoDuplicates(vivas, _.student, DomainError.AGENDA_MULTIPLE_VIVAS)
    } yield Agenda(vivas, teachers, externals, duration)

final case class ScheduleAgenda(vivas: List[VivaScheduled], preference: Int)

object ScheduleAgenda:
  def from(vivas: List[VivaScheduled]): Result[ScheduleAgenda] =
    val totalPreference = vivas.map(_.preference).sum
    Right(ScheduleAgenda(vivas, totalPreference))
