package pj.domain.schedule

import pj.domain.Result
import pj.io.AgendaIO.{createAgenda, createAgendaOut}
import pj.xml.DomainToXML.scheduleToXml

import scala.xml.Elem


object ScheduleMS01 extends Schedule:

  // TODO: Create the code to implement a functional domain model for schedule creation
  //       Use the xml.XML code to handle the xml elements
  //       Refer to https://github.com/scala/scala-xml/wiki/XML-Processing for xml creation
  def create(xml: Elem): Result[Elem] =
    val result = for
      agenda <- createAgenda(xml)
      agendaOut <- createAgendaOut(agenda)
    yield agendaOut
    result match
      case Left(value) => Left(value)
      case Right(value) => Right(scheduleToXml(value))