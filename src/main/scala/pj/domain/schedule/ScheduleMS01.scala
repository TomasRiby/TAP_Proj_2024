package pj.domain.schedule

import scala.xml.Elem
import pj.domain.*
import pj.io.{AgendaIO, FileIO, ResourceIO, ScheduleIO}
import pj.typeUtils.opaqueTypes.opaqueTypes.Preference


object ScheduleMS01 extends Schedule:

  // TODO: Create the code to implement a functional domain model for schedule creation
  //       Use the xml.XML code to handle the xml elements
  //       Refer to https://github.com/scala/scala-xml/wiki/XML-Processing for xml creation
  def create(xml: Elem): Result[Elem] =
    
    for{
      agenda <- AgendaIO.loadAgenda(xml)
      algorithm <- Algorithm.makeTheAlgorithmHappen(agenda)
      elem <- ScheduleIO.createScheduleXML(algorithm, algorithm.map(algo => Preference.toInt(algo.preference)).sum)
      _ = println(elem)
    }yield elem
    Right(xml)