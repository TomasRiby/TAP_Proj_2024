package pj.domain.schedule

import scala.xml.Elem
import pj.domain.*
import pj.io.{AgendaIO, ScheduleIO}


object ScheduleMS03 extends Schedule:

  // TODO: Create the code to implement a functional domain model for schedule creation
  //       Use the xml.XML code to handle the xml elements
  //       Refer to https://github.com/scala/scala-xml/wiki/XML-Processing for xml creation

  def create(xml: Elem): Result[Elem] =
    for {
      agenda <- AgendaIO.loadAgenda(xml)
      algorithmMs03 <- Algorithm.MS01_Algorithm(agenda)
      res <- ScheduleIO.createScheduleXML(algorithmMs03)
    } yield res
