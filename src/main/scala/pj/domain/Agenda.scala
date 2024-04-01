package pj.domain

import pj.io.FileIO
import pj.xml.XML

import scala.xml.{Elem, Node}

final case class Agenda()

object Agenda:
  def loadTeachers(xmlData: String): Any =
    val teachersResult = for {
      xml <- FileIO.load(xmlData)
      resources <- XML.fromNode(xml, "resources")
      teachers <- XML.fromNode(resources, "teachers")
    } yield extractTeachers(teachers)

    // Handle the result
    teachersResult match
      case Right(teachers) => teachers.foreach(println(_))
      case Left(error) => println(s"Error: $error")

  // Helper method to extract teacher IDs from the teachers node
  private def extractTeachers(teachersNode: Node): Seq[Teacher] =
    (teachersNode \ "teacher").flatMap { node =>
      for {
        id <- XML.fromAttribute(node, "id").toOption
        name <- XML.fromAttribute(node, "name").toOption
        availability <- Some(extractAvailability(node))
      } yield Teacher(id, name, availability)
    }

  private def extractAvailability(availabilityNode: Node): Seq[Availability] =
    (availabilityNode \ "availability").flatMap { node =>
      for {
        start <- XML.fromAttribute(node, "start").toOption
        end <- XML.fromAttribute(node, "end").toOption
        preference <- XML.fromAttribute(node, "preference").toOption
      } yield Availability(start, end, preference)
    }

