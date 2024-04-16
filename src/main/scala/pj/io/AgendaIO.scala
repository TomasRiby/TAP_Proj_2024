package pj.io

import pj.domain.{Agenda, External, Result, Teacher, Viva}
import pj.xml.XML


object AgendaIO:
  def loadAgenda(xmlPath: String): Result[Agenda] =
    println("------------------------------------------------------------------------")
    println(xmlPath)
    for {
      viva <- VivaIO.loadViva(xmlPath)
      resources <- ResourceIO.loadResources(xmlPath)
      loadXml <- FileIO.load(xmlPath)
      durationXml <- XML.fromAttribute(loadXml, "duration")
    } yield Agenda.from(viva,resources, durationXml)
