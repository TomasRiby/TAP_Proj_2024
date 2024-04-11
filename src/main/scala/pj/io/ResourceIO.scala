package pj.io

import pj.domain.{Availability, External, Result, Teacher}
import pj.xml.XML

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
      id <- XML.fromAttribute(teacherNode, "id")
      name <- XML.fromAttribute(teacherNode, "name")
      availability <- XML.traverse(teacherNode \\ "availability", extractAvailabilities)
    yield Teacher.from(id, name, availability)

  private def extractExternals(externalNode: Node): Result[External] =
    for
      id <- XML.fromAttribute(externalNode, "id")
      name <- XML.fromAttribute(externalNode, "name")
      availability <- XML.traverse(externalNode \\ "availability", extractAvailabilities)
    yield External.from(id, name, availability)


  private def extractAvailabilities(availabilityNode: Node): Result[Availability] =
    for
      start <- XML.fromAttribute(availabilityNode, "start")
      end <- XML.fromAttribute(availabilityNode, "end")
      preference <- XML.fromAttribute(availabilityNode, "preference")
    yield Availability.from(start, end, preference)
  
  
