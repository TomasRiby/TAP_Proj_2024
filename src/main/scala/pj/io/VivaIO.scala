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
      supervisors <- XML.traverse(vivaNode \\ "supervisor", extractSupervisor)
      coAdvisors <- XML.traverse(vivaNode \\ "coadvisor", extractCoAdvisor)

    } yield Viva.from(student, title, president, advisor, supervisors, coAdvisors)

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
    } yield Advisor.from(id)

  private def extractSupervisor(supervisorNode: Node): Result[Supervisor] =
    for {
      idXML <- XML.fromAttribute(supervisorNode, "id")
      id <- ID.createRegularId(idXML)
    } yield Supervisor.from(id)


  private def extractCoAdvisor(coadvisorNode: Node): Result[CoAdvisor] =
    for {
      idXML <- XML.fromAttribute(coadvisorNode, "id")
      id <- ID.createRegularId(idXML)
    } yield CoAdvisor.from(id)