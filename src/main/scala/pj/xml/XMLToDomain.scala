package pj.xml

import pj.domain.*
import pj.opaqueTypes.Name
import pj.xml.XML.*

import java.time.LocalDateTime
import scala.util.{Failure, Success, Try}
import scala.xml.Node

object XMLToDomain:
  def parseViva(vivaNode: Node, teachers: List[Teacher], externals: List[External]): Result[VivaNotScheduled] =
    val resourcesIds = List.empty[String]
    for
      studentString <- fromAttribute(vivaNode, "student")
      student <- Name.createName(studentString)

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
        case teacher: Teacher => teacher.id.IDtoString
        case external: External => external.id.IDtoString
      
      // This one doesn't need to validate that the id is different from advisor nor president
      supervisors: List[External] <- parseSupervisorsNode(vivaNode \ "supervisor", externals, coadvisorsIds ::: resourcesIds)

      viva <- VivaNotScheduled.from(student, title, president, advisor, supervisors, coadvisors, teachers, externals)
    yield viva

  private def getIds(coadvisors: List[Teacher | External]): Result[List[String]] =
    Right(coadvisors.map {
      case teacher: Teacher => teacher.id.IDtoString
      case external: External => external.id.IDtoString
    })

  private def getTeacher(id: String, teachers: List[Teacher]): Result[Teacher] =
    teachers.find(teacher => teacher.id.IDtoString == id) match
      case Some(value) => Right(value)
      case None => Left(DomainError.InvalidIdRef(id))

  private def parseSupervisorsNode(nodes: Seq[Node], externals: List[External], resourcesIds: List[String]): Result[List[External]] =
    nodes.foldLeft[Result[Vector[External]]](Right(Vector.empty)) { case (accRes, node) =>
      for
        acc <- accRes
        supervisor <- parseSupervisor(node, externals, resourcesIds)
      yield acc :+ supervisor
    }.map(_.toList)

  private def parseCoadvisorsNode(nodes: Seq[Node], teachers: List[Teacher], externals: List[External], resourcesIds: List[String]): Result[List[Teacher | External]] =
    nodes.foldLeft[Result[Vector[Teacher | External]]](Right(Vector.empty)) { case (accRes, node) =>
      for
        acc <- accRes
        coadvisor <- parseCoadvisor(node, teachers ++ externals, resourcesIds)
      yield acc :+ coadvisor
    }.map(_.toList)

  private def parseSupervisor(node: Node, externals: List[External], resourcesIds: List[String]): Result[External] =
    for
      supervisorId <- fromAttribute(node, "id")
      supervisor <- findIn(supervisorId, externals)
      _ <- moreThanOneRoleValidation(supervisorId, resourcesIds)
    yield supervisor

  private def parseCoadvisor(node: Node, resources: List[Teacher | External], resourcesIds: List[String]): Result[Teacher | External] =
    for
      coadvisorId <- fromAttribute(node, "id")
      coadvisor <- findIn(coadvisorId, resources)
      _ <- moreThanOneRoleValidation(coadvisorId, resourcesIds)
    yield coadvisor

  private def findIn[R <: Teacher | External](id: String, resources: List[R]): Result[R] =
    val resource = resources.find(resource =>
      resource match
        case teacher: Teacher => teacher.id.IDtoString == id
        case external: External => external.id.IDtoString == id
    )
    resource match
      case Some(value) => Right(value)
      case None => Left(DomainError.InvalidIdRef(id))

  private def moreThanOneRoleValidation(id: String, resourcesIds: List[String]): Result[String] =
    if (resourcesIds.nonEmpty && resourcesIds.contains(id)) Left(DomainError.MoreThanOneRole(s"The resource with id $id can't exercise more than one role"))
    else Right(id)