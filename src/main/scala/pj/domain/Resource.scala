package pj.domain

import scala.annotation.targetName
import scala.util.matching.Regex

opaque type TeacherId = String

object TeacherId:

  private val teacherIdPattern: Regex = "^T[0-9]{3}$".r

  private def isValidTeacherId(id: String): Result[Unit] =
    if teacherIdPattern.matches(id) then Right({})
    else Left(DomainError.InvalidTeacherId(id))

  def from(id: String): Result[TeacherId] =
    for {
      _ <- isValidTeacherId(id)
    } yield id

  extension (id: TeacherId)
    @targetName("TeacherId.toString")
    def TeacherIdToString: String = id


opaque type Name = String

object Name:

  private def isValidName(name: String): Result[Unit] =
    if (name.isBlank) Left(DomainError.InvalidName)
    else Right(())

  def from(name: String): Result[Name] =
    for {
      _ <- isValidName(name)
    } yield name

  extension (name: Name)
    @targetName("Name.toString")
    def toString: String = name

opaque type ExternalId = String

object ExternalId:

  private val externalIdPattern: Regex = "^E[0-9]{3}$".r

  private def isValidExternalId(id: String): Result[Unit] =
    if externalIdPattern.matches(id) then Right({})
    else Left(DomainError.InvalidExternalPersonId(id))

  def from(id: String): Result[ExternalId] =
    for {
      _ <- isValidExternalId(id)
    } yield id

  extension (id: ExternalId)
    @targetName("ExternalId.toString")
    def ExternalIdToString: String = id

private def isValidAvailabilities(availabilities: List[Availability]): Result[Unit] =
  val availZipped = availabilities.zipWithIndex
  if !availZipped.exists { case (availability1, index1) =>
    availZipped.exists { case (availability2, index2) =>
      index1 != index2 &&
        !(availability1.interval.precedes(availability2.interval) || availability1.interval.meets(availability2.interval)
          || availability2.interval.precedes(availability1.interval)  || availability2.interval.meets(availability1.interval))
    }
  } then Right(()) else Left(DomainError.OverlappingAvailabilities)

sealed trait Resource:
  def name: Name

  def availabilities: List[Availability]

final case class Teacher(id: TeacherId, name: Name, availabilities: List[Availability]) extends Resource

object Teacher:
  def from(id: TeacherId, name: Name, availabilities: List[Availability]): Result[Teacher] =
    for {
      _ <- isValidAvailabilities(availabilities)
    } yield Teacher(id, name, availabilities)

final case class External(id: ExternalId, name: Name, availabilities: List[Availability]) extends Resource

object External:
  def from(id: ExternalId, name: Name, availabilities: List[Availability]): Result[External] =
    for {
      _ <- isValidAvailabilities(availabilities)
    } yield External(id, name, availabilities)
