package pj.io

import pj.domain.{Availability, DomainError, External, Resource, Result, Teacher}
import pj.typeUtils.opaqueTypes.opaqueTypes.*
import pj.xml.XML

import java.time.LocalDateTime
import scala.xml.{Elem, Node}

object ResourceIO:

  def loadResources(xml: Elem): Result[Resource] =
    for {
      resultTeachers <- XML.traverse(xml \\ "teacher", extractTeachers)
      _ <- ID.verifyId(resultTeachers)
      resultExternals <- XML.traverse(xml \\ "external", extractExternals)
      _ <- ID.verifyId(resultExternals)
    } yield Resource.from(resultTeachers,resultExternals)

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
      start <- Time.createTime(startXML)
      endXML <- XML.fromAttribute(availabilityNode, "end")
      end <- Time.createTime(endXML)
      preferenceXML <- XML.fromAttribute(availabilityNode, "preference")
      preference <- Preference.createPreference(preferenceXML.toInt)
      availability <- Availability.from(start, end, preference)
    yield availability
  
  
