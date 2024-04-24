package pj.io

import pj.domain.*
import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, Name}
import pj.xml.XML

import scala.language.adhocExtensions
import scala.xml.{Elem, Node}

object VivaIO:

  def loadViva(xml: Elem): Result[Seq[Viva]] =
    for {
      resultVivas <- XML.traverse(xml \\ "viva", extractViva)
    } yield resultVivas

  private def extractViva(vivaNode: Node): Result[Viva] =
    for {
      studentXML <- XML.fromAttribute(vivaNode, "student")
      student <- Name.createName(studentXML)
      titleXML <- XML.fromAttribute(vivaNode, "title")
      title <- Name.createName(titleXML)
      president <- extractPresident(vivaNode)
      advisor <- extractAdvisor(vivaNode)
      supervisor <- extractSupervisor(vivaNode)

    } yield Viva.from(student, title, president, advisor, supervisor)

  private def extractPresident(node: Node): Result[President] =
    for {
      president <- XML.fromNode(node, "president")
      idXML <- XML.fromAttribute(president, "id")
      id <- ID.createRegularId(idXML)

    } yield President.from(id)


  private def extractAdvisor(node: Node): Result[Advisor] =
    for {
      advisor <- XML.fromNode(node, "advisor")
      idXML <- XML.fromAttribute(advisor, "id")
      id <- ID.createRegularId(idXML)
      name <- Name.createName("vl")
    } yield Advisor.from(id, name, List.empty[Availability])

  private def extractSupervisor(node: Node): Result[Supervisor] =
    for {
      supervisor <- XML.fromNode(node, "supervisor")
      idXML <- XML.fromAttribute(supervisor, "id")
      id <- ID.createRegularId(idXML)
      name <- Name.createName("vl2")
    } yield Supervisor.from(id, name, List.empty[Availability])