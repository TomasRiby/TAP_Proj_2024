package pj.xml

import pj.domain.*
import pj.xml.XML.*

import java.time.LocalDateTime
import scala.util.{Failure, Success, Try}
import scala.xml.Node

object XMLToDomain:
  def parseTeacher(node: Node): Result[Teacher] =
    for
      idString <- fromAttribute(node, "id")
      id <- TeacherId.from(idString)
      nameString <- fromAttribute(node, "name")
      name <- Name.from(nameString)
      availabilities <- traverse(node \ "availability", node => parseAvailability(node))
      teacher <- Teacher.from(id, name, availabilities)
    yield teacher

  def parseExternal(node: Node): Result[External] =
    for
      idString <- fromAttribute(node, "id")
      id <- ExternalId.from(idString)
      nameString <- fromAttribute(node, "name")
      name <- Name.from(nameString)
      availabilities <- traverse(node \ "availability", node => parseAvailability(node))
      external <- External.from(id, name, availabilities)
    yield external

  private def parseAvailability(node: Node): Result[Availability] =
    for
      startString <- fromAttribute(node, "start")
      start <- DateTime.from(startString)

      endString <- fromAttribute(node, "end")
      end <- DateTime.from(endString)

      preferenceString <- fromAttribute(node, "preference")
      preference <- Preference.from(preferenceString)

      availability <- Availability.from(start, end, preference)
    yield availability

  def parseViva(vivaNode: Node, teachers: List[OTeacher], externals: List[OExternal]): Result[VivaNotScheduled] =
    val resourcesIds = List.empty[String]
    for
      studentString <- fromAttribute(vivaNode, "student")
      student <- Name.from(studentString)

      titleString <- fromAttribute(vivaNode, "title")
      title <- Title.from(titleString)

      presidentNode <- fromNode(vivaNode, "president")
      presidentId <- fromAttribute(presidentNode, "id")
      president <- getTeacher(presidentId, teachers)

      advisorNode <- fromNode(vivaNode, "advisor")
      advisorId <- fromAttribute(advisorNode, "id")
      advisor <- getTeacher(advisorId, teachers)
      _ <- moreThanOneRoleValidation(advisorId, presidentId :: resourcesIds)

      coadvisors <- parseCoadvisorsNode(vivaNode \ "coadvisor", teachers, externals, presidentId :: advisorId :: resourcesIds)
      coadvisorsIds = coadvisors.map:
        case teacher: OTeacher => teacher.id.IDtoString
        case external: OExternal => external.id.IDtoString
      
      // This one doesn't need to validate that the id is different from advisor nor president
      supervisors: List[OExternal] <- parseSupervisorsNode(vivaNode \ "supervisor", externals, coadvisorsIds ::: resourcesIds)

      viva <- VivaNotScheduled.from(student, title, president, advisor, supervisors, coadvisors, teachers, externals)
    yield viva

  private def getIds(coadvisors: List[Resource]): Result[List[String]] =
    Right(coadvisors.map {
      case teacher: Teacher => teacher.id.TeacherIdToString
      case external: External => external.id.ExternalIdToString
    })

  private def getTeacher(id: String, teachers: List[OTeacher]): Result[OTeacher] =
    teachers.find(teacher => teacher.id.IDtoString == id) match
      case Some(value) => Right(value)
      case None => Left(DomainError.InvalidIdRef(id))

  private def parseSupervisorsNode(nodes: Seq[Node], externals: List[OExternal], resourcesIds: List[String]): Result[List[OExternal]] =
    nodes.foldLeft[Result[Vector[OExternal]]](Right(Vector.empty)) { case (accRes, node) =>
      for
        acc <- accRes
        supervisor <- parseSupervisor(node, externals, resourcesIds)
      yield acc :+ supervisor
    }.map(_.toList)

  private def parseCoadvisorsNode(nodes: Seq[Node], teachers: List[OTeacher], externals: List[OExternal], resourcesIds: List[String]): Result[List[OTeacher | OExternal]] =
    nodes.foldLeft[Result[Vector[OTeacher | OExternal]]](Right(Vector.empty)) { case (accRes, node) =>
      for
        acc <- accRes
        coadvisor <- parseCoadvisor(node, teachers ++ externals, resourcesIds)
      yield acc :+ coadvisor
    }.map(_.toList)

  private def parseSupervisor(node: Node, externals: List[OExternal], resourcesIds: List[String]): Result[OExternal] =
    for
      supervisorId <- fromAttribute(node, "id")
      supervisor <- findIn(supervisorId, externals)
      _ <- moreThanOneRoleValidation(supervisorId, resourcesIds)
    yield supervisor

  private def parseCoadvisor(node: Node, resources: List[OTeacher | OExternal], resourcesIds: List[String]): Result[OTeacher | OExternal] =
    for
      coadvisorId <- fromAttribute(node, "id")
      coadvisor <- findIn(coadvisorId, resources)
      _ <- moreThanOneRoleValidation(coadvisorId, resourcesIds)
    yield coadvisor

  private def findIn[R <: OTeacher | OExternal](id: String, resources: List[R]): Result[R] =
    val resource = resources.find(resource =>
      resource match
        case teacher: OTeacher => teacher.id.IDtoString == id
        case external: OExternal => external.id.IDtoString == id
    )
    resource match
      case Some(value) => Right(value)
      case None => Left(DomainError.InvalidIdRef(id))

  private def moreThanOneRoleValidation(id: String, resourcesIds: List[String]): Result[String] =
    if (resourcesIds.nonEmpty && resourcesIds.contains(id)) Left(DomainError.MoreThanOneRole(s"The resource with id $id can't exercise more than one role"))
    else Right(id)