package pj.io

import pj.domain.{Agenda, AgendaOut, Result, Time}
import VivaIO.scheduleVivas
import pj.domain.myDomain.OAgenda
import pj.xml.XML.{fromAttribute, fromNode, traverse}
import pj.xml.XMLToDomain.{parseExternal, parseTeacher, parseViva}

import scala.xml.Elem


object AgendaIO:

  def loadAgenda(xml: Elem): Result[OAgenda] =
    for

      durationString <- fromAttribute(xml, "duration")
      duration <- Time.from(durationString)

      resources <- fromNode(xml, "resources")

      teachersNode <- fromNode(resources, "teachers")
      teachers <- traverse(teachersNode \ "teacher", node => parseTeacher(node))


      externalsNode <- fromNode(resources, "externals")
      externals <- traverse(externalsNode \ "external", node => parseExternal(node))

      // Estes funcionam
      lalaTeachers <- ResourceIO.loadTeachers(xml)
      lalaExternals <- ResourceIO.loadExternals(xml)


      vivasNode <- fromNode(xml, "vivas")
      vivas <- traverse(vivasNode \ "viva", node => parseViva(node, lalaTeachers, lalaExternals))

      agenda <- OAgenda.from(vivas, lalaTeachers, lalaExternals, duration)
    yield agenda

  def createAgendaOut(agenda: OAgenda): Result[AgendaOut] =
    for {
      vivas <- scheduleVivas(agenda)
      agendaOut <- AgendaOut.from(vivas)
    } yield agendaOut
