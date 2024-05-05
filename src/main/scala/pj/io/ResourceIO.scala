package pj.io

import pj.domain.*
import pj.io.AvailabilityIO.updateAvailabilities

object ResourceIO:

  def updateTeachers(timeSlot: Interval, teachers: List[Teacher], vivaTeachers: List[Teacher]): Result[List[Teacher]] =
    val newTeachers = teachers.filter(teacher => !vivaTeachers.contains(teacher))

    def updateTeachersAux(timeSlot: Interval, teachers: List[Teacher]): Result[List[Teacher]] =
      teachers.foldLeft[Result[Vector[Teacher]]](Right(Vector.empty[Teacher])) { case (accRes, teacher) =>
        for
          acc <- accRes

          updatedTeacher <- updateTeacher(timeSlot, teacher)
        yield acc :+ updatedTeacher
      }.map(_.toList)

    val updatedTeachers = updateTeachersAux(timeSlot, vivaTeachers)
    updatedTeachers match
      case Left(value) => Left(DomainError.ImpossibleSchedule)
      case Right(value) => Right(value ++ newTeachers)


  private def updateTeacher(timeSlot: Interval, teacher: Teacher): Result[Teacher] =
    for {
      availabilities <- updateAvailabilities(timeSlot, teacher.availabilities)
      updatedTeacher <- Teacher.from(teacher.id, teacher.name, availabilities)
    } yield updatedTeacher

  def updateExternals(timeSlot: Interval, externals: List[External], vivaExternals: List[External]): Result[List[External]] =
    val newExternals = externals.filter(external => !vivaExternals.contains(external))

    def updateExternalsAux(timeSlot: Interval, externals: List[External]): Result[List[External]] =
      externals.foldLeft[Result[Vector[External]]](Right(Vector.empty[External])) { case (accRes, external) =>
        for
          acc <- accRes

          updatedExternal <- updateExternal(timeSlot, external)
        yield acc :+ updatedExternal
      }.map(_.toList)

    val updatedExternals = updateExternalsAux(timeSlot, vivaExternals)
    updatedExternals match
      case Left(value) => Left(DomainError.ImpossibleSchedule)
      case Right(value) => Right(value ++ newExternals)

  private def updateExternal(timeSlot: Interval, external: External): Result[External] =
    for {
      availabilities <- updateAvailabilities(timeSlot, external.availabilities)
      updatedExternal <- External.from(external.id, external.name, availabilities)
    } yield updatedExternal

  def calculatePreference(interval: Interval, resources: List[Resource]): Result[Int] =
    val res = resources.flatMap(_.availabilities)
      .filter(availability =>
        interval.isPartOf(availability.interval)
      ).map(_.preference.to).sum
    Right(res)