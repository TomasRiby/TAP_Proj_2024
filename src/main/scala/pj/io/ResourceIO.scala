package pj.io

import pj.domain.{Availability, Resource}
import pj.xml.XML

import scala.xml.Node

object ResourceIO:
  def loadTeachers(xmlData: String): Seq[Resource] =
    val teachersResult = for {
      xml <- FileIO.load(xmlData)
      resources <- XML.fromNode(xml, "resources")
      teachers <- XML.fromNode(resources, "teachers")
    } yield extractTeachers(teachers)

    // Handle the result
    teachersResult match
      case Right(teachers) => teachers

  def loadExternals(xmlData: String): Seq[Resource] =
    val externalsResult = for {
      xml <- FileIO.load(xmlData)
      resources <- XML.fromNode(xml, "resources")
      externals <- XML.fromNode(resources, "externals")
    } yield extractExternals(externals)

    // Handle the result
    externalsResult match
      case Right(externals) => externals


  // Helper method to extract teacher IDs from the teachers node
  private def extractTeachers(teachersNode: Node): Seq[Resource] =
    (teachersNode \ "teacher").flatMap { node =>
      for {
        id <- XML.fromAttribute(node, "id").toOption
        name <- XML.fromAttribute(node, "name").toOption
        availability <- Some(extractAvailability(node))
      } yield Resource.from(id, name, availability, "TEACHER")
    }

  private def extractExternals(externalNode: Node): Seq[Resource] =
    (externalNode \ "external").flatMap { node =>
      for {
        id <- XML.fromAttribute(node, "id").toOption
        name <- XML.fromAttribute(node, "name").toOption
        availability <- Some(extractAvailability(node))
      } yield Resource.from(id, name, availability, "EXTERNAL")
    }

  private def extractAvailability(availabilityNode: Node): Seq[Availability] =
    (availabilityNode \ "availability").flatMap { node =>
      for {
        start <- XML.fromAttribute(node, "start").toOption
        end <- XML.fromAttribute(node, "end").toOption
        preference <- XML.fromAttribute(node, "preference").toOption
      } yield Availability(start, end, preference)
    }
  
