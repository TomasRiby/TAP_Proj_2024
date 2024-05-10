package pj.io

import pj.domain.{AgendaOut, Agenda, Result}
import VivaIO.scheduleVivas
import pj.opaqueTypes.ODuration
import pj.opaqueTypes.OTime
import pj.xml.XML.{fromAttribute, fromNode, traverse}
import pj.xml.XMLToDomain.parseViva

import scala.xml.Elem


object AgendaIO:

  def loadAgenda(xml: Elem): Result[Agenda] =
    for

      durationString <- fromAttribute(xml, "duration")
      duration <- ODuration.createDuration(durationString)

      loadedTeachers <- ResourceIO.loadTeachers(xml)
      loadedExternals <- ResourceIO.loadExternals(xml)

      vivasNode <- fromNode(xml, "vivas")
      vivas <- traverse(vivasNode \ "viva", node => parseViva(node, loadedTeachers, loadedExternals))

      agenda <- Agenda.from(vivas, loadedTeachers, loadedExternals, duration)
    yield agenda

  def createAgendaOut(agenda: Agenda): Result[AgendaOut] =
    for {
      vivas <- scheduleVivas(agenda)
      agendaOut <- AgendaOut.from(vivas)
    } yield agendaOut
