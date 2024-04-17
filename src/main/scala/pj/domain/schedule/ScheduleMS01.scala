package pj.domain.schedule

import scala.xml.Elem
import pj.domain.*
import pj.io.{AgendaIO, FileIO, ResourceIO}

import pj.io.ScheduleIO.planToXml

object ScheduleMS01 extends Schedule:

  // TODO: Create the code to implement a functional domain model for schedule creation
  //       Use the xml.XML code to handle the xml elements
  //       Refer to https://github.com/scala/scala-xml/wiki/XML-Processing for xml creation
  def create(xml: Elem): Result[Elem] =
    println(xml)
    val agenda = AgendaIO.loadAgenda(xml)
    println(agenda)

    Right(xml)
    //for
    //yield pl