package pj.domain

import pj.io.FileIO

import scala.xml.Node
import pj.xml.XML

final case class Teacher(
                          id: String,
                          name: String,
                          availability: Seq[Availability]
                        )

object Teacher:
  def loadTeachers(xmlData: String): Seq[Teacher] =
    val teachersResult = for {
      xml <- FileIO.load(xmlData)
      resources <- XML.fromNode(xml, "resources")
      teachers <- XML.fromNode(resources, "teachers")
    } yield extractTeachers(teachers)

    // Handle the result
    teachersResult match
      case Right(teachers) => teachers

  // Helper method to extract teacher IDs from the teachers node
  private def extractTeachers(teachersNode: Node): Seq[Teacher] =
    (teachersNode \ "teacher").flatMap { node =>
      for {
        id <- XML.fromAttribute(node, "id").toOption
        name <- XML.fromAttribute(node, "name").toOption
        availability <- Some(Availability.extractAvailability(node))
      } yield Teacher(id, name, availability)
    }





