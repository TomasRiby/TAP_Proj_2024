package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.{Agenda, Availability, President, Teacher, Viva}
import pj.io.FileIO
import pj.xml.XML

import scala.language.adhocExtensions
import scala.xml.Node

class ExtractingVivaTest extends AnyFunSuite:

  test("Extracting Viva"):
    val filePath = "files/assessment/ms01/valid_agenda_01_in.xml"
    val vivaResult = for {
      xml <- FileIO.load(filePath)
      vivas <- XML.fromNode(xml, "vivas")
    } yield extractVivas(vivas)

    vivaResult match
      case Right(ids) => ids.foreach(println(_))
      case Left(error) => println(s"Error: $error")

  // Helper method to extract teacher IDs from the teachers node
  private def extractVivas(vivasNode: Node): Seq[Viva] =
    (vivasNode \ "viva").flatMap { node =>
      for {
        student <- XML.fromAttribute(node, "student").toOption
        title <- XML.fromAttribute(node, "title").toOption
        president <- Some(extractPresident(node))
        advisor <- Some(extractAdvisor(node))
        supervisor <- Some(extractSupervisor(node))

      } yield Viva(student, title, president,advisor,supervisor)
    }

  def extractPresident(presidentNode: Node): Seq[President] =
    (presidentNode \ "president").flatMap { node =>
      for {
        id <- XML.fromAttribute(node, "id").toOption
      } yield President(id)
    }

  def extractAdvisor(presidentNode: Node): Seq[President] =
    (presidentNode \ "advisor").flatMap { node =>
      for {
        id <- XML.fromAttribute(node, "id").toOption
      } yield President(id)
    }

  def extractSupervisor(presidentNode: Node): Seq[President] =
    (presidentNode \ "supervisor").flatMap { node =>
      for {
        id <- XML.fromAttribute(node, "id").toOption
      } yield President(id)
    }





