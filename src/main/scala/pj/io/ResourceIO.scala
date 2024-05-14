package pj.io

import pj.domain.*
import pj.domain.{Availability, Period}
import pj.io.AvailabilityIO.updateAvailabilities
import pj.opaqueTypes.{ID, Name, Preference, OTime}
import pj.xml.XML
import scala.xml.{Elem, Node}

object ResourceIO:


  def loadTeachers(xml: Elem): Result[List[Teacher]] =
    for {
      resultTeachers <- XML.traverse(xml \\ "teacher", extractTeachers)
      _ <- ID.verifyId(resultTeachers)
    } yield resultTeachers

  def loadExternals(xml: Elem): Result[List[External]] =
    for {
      resultExternals <- XML.traverse(xml \\ "external", extractExternals)
      _ <- ID.verifyId(resultExternals)
    } yield resultExternals


  private def extractTeachers(teacherNode: Node): Result[Teacher] =
    for
      idXml <- XML.fromAttribute(teacherNode, "id")
      id <- ID.createTeacherId(idXml)
      nameXml <- XML.fromAttribute(teacherNode, "name")
      name <- Name.createName(nameXml)
      availability <- XML.traverse(teacherNode \\ "availability", extractAvailabilities)
    yield Teacher.from(id, name, availability)

  private def extractExternals(externalNode: Node): Result[External] =
    for
      xmlId <- XML.fromAttribute(externalNode, "id")
      id <- ID.createExternalId(xmlId)
      nameXml <- XML.fromAttribute(externalNode, "name")
      name <- Name.createName(nameXml)
      availability <- XML.traverse(externalNode \\ "availability", extractAvailabilities)
    yield External.from(id, name, availability)

  private def extractAvailabilities(availabilityNode: Node): Result[Availability] =
    for
      startXML <- XML.fromAttribute(availabilityNode, "start")
      start <- OTime.createTime(startXML)
      endXML <- XML.fromAttribute(availabilityNode, "end")
      end <- OTime.createTime(endXML)
      period <- Period.from(start, end)
      preferenceXML <- XML.fromAttribute(availabilityNode, "preference")
      preference <- Preference.createPreference(preferenceXML.toInt)
    yield Availability.from(period, preference)

  def updateTeachers(timeSlot: Period, teachers: List[Teacher], vivaTeachers: List[Teacher]): Result[List[Teacher]] =
    val newTeachers = teachers.filter(teacher => !vivaTeachers.contains(teacher))

    def updateTeachersAux(timeSlot: Period, teachers: List[Teacher]): Result[List[Teacher]] =
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


  private def updateTeacher(timeSlot: Period, teacher: Teacher): Result[Teacher] =
    for {
      availabilities <- updateAvailabilities(timeSlot, teacher.availability)
    } yield Teacher.from(teacher.id, teacher.name, availabilities)

  def updateExternals(timeSlot: Period, externals: List[External], vivaExternals: List[External]): Result[List[External]] =
    val newExternals = externals.filter(external => !vivaExternals.contains(external))

    def updateExternalsAux(timeSlot: Period, externals: List[External]): Result[List[External]] =
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

  private def updateExternal(timeSlot: Period, external: External): Result[External] =
    for {
      availabilities <- updateAvailabilities(timeSlot, external.availability)
    } yield External.from(external.id, external.name, availabilities)

  def calculatePreference(interval: Period, resources: List[Teacher | External]): Result[Int] =
    val res = resources.flatMap {
      case teacher: Teacher => teacher.availability
      case external: External => external.availability
    }.filter(availability =>
      interval.isPartOf(availability.period)
    ).map(_.preference.toInteger).sum
    Right(res)

  def getIds(coadvisors: List[Teacher | External]): Result[List[String]] =
    Right(coadvisors.map {
      case teacher: Teacher => teacher.id.IDtoString
      case external: External => external.id.IDtoString
    })

  def getTeacher(id: String, teachers: List[Teacher]): Result[Teacher] =
    teachers.find(teacher => teacher.id.IDtoString == id) match
      case Some(value) => Right(value)
      case None => Left(DomainError.TEACHER_INVALID_ID(id))

  def parseSupervisorsNode(nodes: Seq[Node], externals: List[External], resourcesIds: List[String]): Result[List[External]] =
    nodes.foldLeft[Result[Vector[External]]](Right(Vector.empty)) { case (accRes, node) =>
      for
        acc <- accRes
        supervisor <- parseSupervisor(node, externals, resourcesIds)
      yield acc :+ supervisor
    }.map(_.toList)

  def parseCoadvisorsNode(nodes: Seq[Node], teachers: List[Teacher], externals: List[External], resourcesIds: List[String]): Result[List[Teacher | External]] =
    nodes.foldLeft[Result[Vector[Teacher | External]]](Right(Vector.empty)) { case (accRes, node) =>
      for
        acc <- accRes
        coadvisor <- parseCoadvisor(node, teachers ++ externals, resourcesIds)
      yield acc :+ coadvisor
    }.map(_.toList)

  def parseSupervisor(node: Node, externals: List[External], resourcesIds: List[String]): Result[External] =
    for
      supervisorId <- XML.fromAttribute(node, "id")
      supervisor <- findIn(supervisorId, externals)
      _ <- moreThanOneRoleValidation(supervisorId, resourcesIds)
    yield supervisor

  def parseCoadvisor(node: Node, resources: List[Teacher | External], resourcesIds: List[String]): Result[Teacher | External] =
    for
      coadvisorId <- XML.fromAttribute(node, "id")
      coadvisor <- findIn(coadvisorId, resources)
      _ <- moreThanOneRoleValidation(coadvisorId, resourcesIds)
    yield coadvisor

  def findIn[R <: Teacher | External](id: String, resources: List[R]): Result[R] =
    val resource = resources.find(resource =>
      resource match
        case teacher: Teacher => teacher.id.IDtoString == id
        case external: External => external.id.IDtoString == id
    )
    resource match
      case Some(value) => Right(value)
      case None => Left(DomainError.TEACHER_INVALID_ID(id))

  def moreThanOneRoleValidation(id: String, resourcesIds: List[String]): Result[String] =
    if (resourcesIds.nonEmpty && resourcesIds.contains(id)) Left(DomainError.VIVA_MULTIPLE_ROLES(s"The resource with id $id can't exercise more than one role"))
    else Right(id)