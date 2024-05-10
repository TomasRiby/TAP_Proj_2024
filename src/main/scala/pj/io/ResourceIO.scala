package pj.io

import pj.domain.*
import pj.domain.myDomain.{OAvailability, OPeriod}
import pj.domain.schedule.OResource
import pj.io.OAvailabilityIO.updateAvailabilities
import pj.opaqueTypes.{OID, OName, OPreference, OTime}
import pj.xml.XML

import scala.xml.{Elem, Node}

object ResourceIO:


  def loadTeachers(xml: Elem): Result[List[OTeacher]] =
    for {
      resultTeachers <- XML.traverse(xml \\ "teacher", extractTeachers)
      _ <- OID.verifyId(resultTeachers)
    } yield resultTeachers

  def loadExternals(xml: Elem): Result[List[OExternal]] =
    for {
      resultExternals <- XML.traverse(xml \\ "external", extractExternals)
      _ <- OID.verifyId(resultExternals)
    } yield resultExternals


  private def extractTeachers(teacherNode: Node): Result[OTeacher] =
    for
      idXml <- XML.fromAttribute(teacherNode, "id")
      id <- OID.createTeacherId(idXml)
      nameXml <- XML.fromAttribute(teacherNode, "name")
      name <- OName.createName(nameXml)
      availability <- XML.traverse(teacherNode \\ "availability", extractAvailabilities)
    yield OTeacher.from(id, name, availability)

  private def extractExternals(externalNode: Node): Result[OExternal] =
    for
      xmlId <- XML.fromAttribute(externalNode, "id")
      id <- OID.createExternalId(xmlId)
      nameXml <- XML.fromAttribute(externalNode, "name")
      name <- OName.createName(nameXml)
      availability <- XML.traverse(externalNode \\ "availability", extractAvailabilities)
    yield OExternal.from(id, name, availability)

  private def extractAvailabilities(availabilityNode: Node): Result[OAvailability] =
    for
      startXML <- XML.fromAttribute(availabilityNode, "start")
      start <- OTime.createTime(startXML)
      endXML <- XML.fromAttribute(availabilityNode, "end")
      end <- OTime.createTime(endXML)
      period <- OPeriod.from(start, end)
      preferenceXML <- XML.fromAttribute(availabilityNode, "preference")
      preference <- OPreference.createPreference(preferenceXML.toInt)
    yield OAvailability.from(period, preference)

  def updateTeachers(timeSlot: OPeriod, teachers: List[OTeacher], vivaTeachers: List[OTeacher]): Result[List[OTeacher]] =
    val newTeachers = teachers.filter(teacher => !vivaTeachers.contains(teacher))

    def updateTeachersAux(timeSlot: OPeriod, teachers: List[OTeacher]): Result[List[OTeacher]] =
      teachers.foldLeft[Result[Vector[OTeacher]]](Right(Vector.empty[OTeacher])) { case (accRes, teacher) =>
        for
          acc <- accRes

          updatedTeacher <- updateTeacher(timeSlot, teacher)
        yield acc :+ updatedTeacher
      }.map(_.toList)

    val updatedTeachers = updateTeachersAux(timeSlot, vivaTeachers)
    updatedTeachers match
      case Left(value) => Left(DomainError.ImpossibleSchedule)
      case Right(value) => Right(value ++ newTeachers)


  private def updateTeacher(timeSlot: OPeriod, teacher: OTeacher): Result[OTeacher] =
    for {
      availabilities <- updateAvailabilities(timeSlot, teacher.availability)
    } yield OTeacher.from(teacher.id, teacher.name, availabilities)

  def updateExternals(timeSlot: OPeriod, externals: List[OExternal], vivaExternals: List[OExternal]): Result[List[OExternal]] =
    val newExternals = externals.filter(external => !vivaExternals.contains(external))

    def updateExternalsAux(timeSlot: OPeriod, externals: List[OExternal]): Result[List[OExternal]] =
      externals.foldLeft[Result[Vector[OExternal]]](Right(Vector.empty[OExternal])) { case (accRes, external) =>
        for
          acc <- accRes

          updatedExternal <- updateExternal(timeSlot, external)
        yield acc :+ updatedExternal
      }.map(_.toList)

    val updatedExternals = updateExternalsAux(timeSlot, vivaExternals)
    updatedExternals match
      case Left(value) => Left(DomainError.ImpossibleSchedule)
      case Right(value) => Right(value ++ newExternals)

  private def updateExternal(timeSlot: OPeriod, external: OExternal): Result[OExternal] =
    for {
      availabilities <- updateAvailabilities(timeSlot, external.availability)
    } yield OExternal.from(external.id, external.name, availabilities)

  def calculatePreference(interval: OPeriod, resources: List[OTeacher | OExternal]): Result[Int] =
    val res = resources.flatMap {
      case teacher: OTeacher => teacher.availability
      case external: OExternal => external.availability
    }.filter(availability =>
      interval.isPartOf(availability.OPeriod)
    ).map(_.preference.toInteger).sum
    Right(res)