package pj.io

import pj.domain.{Agenda, External, Result, Teacher, Viva}
import pj.xml.XML

import scala.xml.Elem


object AgendaIO:
  def loadAgenda(xml: Elem): Result[Agenda] =
    
    for {
      viva <- VivaIO.loadViva(xml)
      resources <- ResourceIO.loadResources(xml)
      durationXml <- XML.fromAttribute(xml, "duration")
    } yield Agenda.from(viva,resources, durationXml)
