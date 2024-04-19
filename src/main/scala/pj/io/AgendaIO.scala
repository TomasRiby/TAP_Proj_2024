package pj.io

import pj.domain.{Agenda, External, Result, Teacher, Viva}
import pj.typeUtils.opaqueTypes.opaqueTypes.{ODuration, Time}
import pj.xml.XML

import scala.xml.Elem


object AgendaIO:
  def loadAgenda(xml: Elem): Result[Agenda] =
    for {
      viva <- VivaIO.loadViva(xml)
      resources <- ResourceIO.loadResources(xml)
      durationXml <- XML.fromAttribute(xml, "duration")
      duration <- ODuration.createDuration(durationXml)
    } yield Agenda.from(viva,resources, duration)

  def loadAgenda(xml: String): Result[Agenda] =
    println("---------------------------")
    println(xml)

    for {
      loadXML <- FileIO.load(xml)
      viva <- VivaIO.loadViva(loadXML)
      resources <- ResourceIO.loadResources(loadXML)
      durationXml <- XML.fromAttribute(loadXML, "duration")
      duration <- ODuration.createDuration(durationXml)
    } yield Agenda.from(viva, resources, duration)
