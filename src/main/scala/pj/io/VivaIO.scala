package pj.io

import pj.domain.*
import pj.xml.XML

import scala.language.adhocExtensions
import scala.xml.Node

object VivaIO:

  def loadViva(xmlData: String): Result[Seq[Viva]] =
    for {
      xml <- FileIO.load(xmlData)
      resultVivas <- XML.traverse(xml \\ "viva", extractViva)
    } yield resultVivas

  private def extractViva(vivaNode: Node): Result[Viva] =
    for {
      student <- XML.fromAttribute(vivaNode, "student")
      title <- XML.fromAttribute(vivaNode, "title")
      president <- extractPresident(vivaNode)
      advisor <- extractAdvisor(vivaNode)
      supervisor <- extractSupervisor(vivaNode)

    } yield Viva(student, title, president, advisor, supervisor)

  private def extractPresident(node: Node): Result[President] =
    for {
      president <- XML.fromNode(node, "president")
      id <- XML.fromAttribute(president, "id")
    } yield President.from(id)


  private def extractAdvisor(node: Node): Result[Advisor] =
    for {
      advisor <- XML.fromNode(node, "advisor")
      id <- XML.fromAttribute(advisor, "id")
    } yield Advisor.from(id)

  private def extractSupervisor(node: Node): Result[Supervisor] =
    for {
      supervisor <- XML.fromNode(node, "supervisor")
      id <- XML.fromAttribute(supervisor, "id")
    } yield Supervisor.from(id)