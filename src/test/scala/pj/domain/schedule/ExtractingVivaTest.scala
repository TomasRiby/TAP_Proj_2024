package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.{Advisor, Agenda, Availability, DomainError, President, Supervisor, Teacher, Viva}
import pj.io.FileIO
import pj.xml.XML

import scala.language.adhocExtensions
import scala.xml.Node

class ExtractingVivaTest extends AnyFunSuite:

  test("Extracting Viva"):

    def loadViva(xmlData: String): Seq[Viva] =
      val vivaResult = for {
        xml <- FileIO.load(xmlData)
        vivas <- XML.fromNode(xml, "vivas")
      } yield extractVivas(vivas)
  
      vivaResult match
        case Right(ids) => ids

  // Helper method to extract teacher IDs from the teachers node
  private def extractVivas(vivasNode: Node): Seq[Viva] =
    (vivasNode \ "viva").flatMap { node =>
      for {
        student <- XML.fromAttribute(node, "student").toOption
        title <- XML.fromAttribute(node, "title").toOption
        president <- Some(extractPresident(node))
        advisor <- Some(extractAdvisor(node))
        supervisor <- Some(extractSupervisor(node))

      } yield Viva(student, title, president, advisor, supervisor)
    }

  def extractPresident(presidentNode: Node): President =

    val president = for {
      presidentNode <- XML.fromNode(presidentNode, "president")
      presidentId <- XML.fromAttribute(presidentNode, "id")
    } yield presidentId
    president match
      case Right(ids) => President(ids)


  def extractAdvisor(advisorNode: Node): Advisor =

    val advisor = for {
      advisorNode <- XML.fromNode(advisorNode, "advisor")
      advisorId <- XML.fromAttribute(advisorNode, "id")
    } yield advisorId
    advisor match
      case Right(id) => Advisor(id)

  def extractSupervisor(supervisorNode: Node): Supervisor =

    val supervisor = for {
      supervisorNode <- XML.fromNode(supervisorNode, "president")
      supervisorId <- XML.fromAttribute(supervisorNode, "id")
    } yield supervisorId
    supervisor match
      case Right(id) => Supervisor(id)





