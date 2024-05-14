package pj.io

import pj.domain.{ScheduleAgenda, Agenda, Result}
import VivaIO.scheduleVivas
import pj.opaqueTypes.ODuration
import pj.opaqueTypes.OTime
import pj.xml.XML.{fromAttribute, fromNode, traverse}

import scala.xml.Elem


object AgendaIO:

  def loadAgenda(xml: Elem): Result[Agenda] =
    for
      durationString <- fromAttribute(xml, "duration")
      duration <- ODuration.createDuration(durationString)
      loadedTeachers <- ResourceIO.loadTeachers(xml)
      loadedExternals <- ResourceIO.loadExternals(xml)
      vivasNode <- fromNode(xml, "vivas")
      vivas <- traverse(vivasNode \ "viva", node => VivaIO.loadViva(node, loadedTeachers, loadedExternals))
      agenda <- Agenda.from(vivas, loadedTeachers, loadedExternals, duration)
    yield agenda

  def agenda_output(agenda: Agenda): Result[ScheduleAgenda] =
    for {
      vivas <- scheduleVivas(agenda)
      agendaOut <- ScheduleAgenda.from(vivas)
    } yield agendaOut
