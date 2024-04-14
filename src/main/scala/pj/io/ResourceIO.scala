package pj.io

import pj.domain.{Availability, External, Result, Teacher}
import pj.typeUtils.opaqueTypes.opaqueTypes.*
import pj.xml.XML

import java.time.LocalDateTime
import scala.xml.Node

object ResourceIO:

  def loadResources(xmlData: String): Result[Seq[External | Teacher]] =
    for {
      xml <- FileIO.load(xmlData)
      resultTeachers <- XML.traverse(xml \\ "teacher", extractTeachers)
      resultExternals <- XML.traverse(xml \\ "external", extractExternals)
    } yield resultExternals ++ resultTeachers

  private def extractTeachers(teacherNode: Node): Result[Teacher] =
    for
      idXml <- XML.fromAttribute(teacherNode, "id")
      id <- ID.createTeacherId(idXml)
      nameXml <- XML.fromAttribute(teacherNode, "name")
      name <- Name.createName(nameXml)
      availability <- XML.traverse(teacherNode \\ "availability", extractAvailabilities)
      teacher <- Teacher.from(id, name, availability, List(""))
    yield teacher

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
      start <- XML.fromAttribute(availabilityNode, "start").map(LocalDateTime.parse)
      end <- XML.fromAttribute(availabilityNode, "end").map(LocalDateTime.parse)
      preferenceXML <- XML.fromAttribute(availabilityNode, "preference")
      preference <- Preference.createPreference(preferenceXML)
    yield Availability.from(start, end, preference)
  
  
