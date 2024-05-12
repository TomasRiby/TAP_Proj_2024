package pj.io

import pj.domain.*
import pj.io.VivaIO.extractViva
import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, Name, Preference, Time}
import pj.xml.XML

import scala.language.adhocExtensions
import scala.xml.{Elem, Node}

object VivaIO:

  def loadViva(xml: Elem): Result[List[Viva]] =
    for {
      resultVivas <- extractViva(xml)
    } yield resultVivas

  private def extractViva(xml: Node): Result[List[Viva]] =
    XML.fromNode(xml, "vivas").flatMap { vivasNode =>
      XML.traverse((xml \\ "viva"), vivaNode => {
        val presidents = XML.fromNode(vivaNode, "president").flatMap { presidentNode =>
          XML.fromAttribute(presidentNode, "id")
        }

        val advisors = XML.fromNode(vivaNode, "advisor").flatMap { advisorNode =>
          XML.fromAttribute(advisorNode, "id")
        }

        val coadvisorIds = XML.traverse((vivaNode \ "coadvisor").toList, coadvisor => {
          XML.fromAttribute(coadvisor, "id")
        })

        val supervisorIds = XML.traverse((vivaNode \ "supervisor").toList, supervisor => {
          XML.fromAttribute(supervisor, "id")
        })

        val allIds = (presidents, advisors, coadvisorIds, supervisorIds) match {
          case (Right(pres), Right(adv), Right(coad), Right(sup)) =>
            List(pres, adv) ++ coad ++ sup
          case _ => List.empty[String]
        }

        for {
          studentXML <- XML.fromAttribute(vivaNode, "student")
          student <- Name.createName(studentXML)
          titleXML <- XML.fromAttribute(vivaNode, "title")
          title <- Name.createName(titleXML)
          teachers <- extractTeachers(xml, vivaNode, allIds)
          externals <- extractExternals(xml, vivaNode, allIds)
        } yield Viva.from(student, title, teachers ++ externals)
      })
    }


  private def extractExternals(xml: Node, vivaNode: Node, ids: List[String]): Result[List[Resource]] =
    XML.traverse(xml \\ "external", external => {
        for {
          idXML <- XML.fromAttribute(external, "id")
          id <- ID.createExternalId(idXML)
          nameXML <- XML.fromAttribute(external, "name")
          externalName <- Name.createName(nameXML)
          availabilities <- XML.traverse((external \ "availability"), extractAvailability)
          resourceType <- extractResourceType(vivaNode, id)
        } yield if (ids.contains(id.toString) && resourceType != ResourseType.None) {
          Some(Resource.from(id, externalName, availabilities, resourceType))
        } else {
          None
        }
    }).map(_.flatten)

  private def extractResourceType(vivaNode: Node, id: ID): Result[ResourseType] =
    val child = vivaNode.child
    val nodeMap = child.map { node =>
      val label = node.label
      val nodeId = (node \ "@id").text
      (nodeId, label)
    }.toMap

    nodeMap.get(id.toString) match
      case Some("president") => Right(ResourseType.President)
      case Some("advisor") => Right(ResourseType.Advisor)
      case Some("supervisor") => Right(ResourseType.Supervisor)
      case _ => Right(ResourseType.None)

  private def extractTeachers(xml: Node, vivaNode: Node, ids: List[String]): Result[List[Resource]] =
    XML.traverse(xml \\ "teacher", teacher => {
      for {
        idXML <- XML.fromAttribute(teacher, "id")
        id <- ID.createTeacherId(idXML)
        nameXML <- XML.fromAttribute(teacher, "name")
        externalName <- Name.createName(nameXML)
        availabilities <- XML.traverse((teacher \ "availability"), extractAvailability)
        resourceType <- extractResourceType(vivaNode, id)
      } yield if (ids.contains(id.toString)) {
        Some(Resource.from(id, externalName, availabilities, resourceType))
      } else {
        None
      }
    }).map(_.flatten)

  private def extractAvailability(xml: Node): Result[Availability] =
      for
        startXML <- XML.fromAttribute(xml, "start")
        start <- Time.createTime(startXML)
        endXML <- XML.fromAttribute(xml, "end")
        end <- Time.createTime(endXML)
        preferenceXML <- XML.fromAttribute(xml, "preference")
        preference <- Preference.createPreference(preferenceXML.toInt)
      yield Availability(start, end, preference)