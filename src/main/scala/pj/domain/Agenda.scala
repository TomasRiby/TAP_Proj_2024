package pj.domain

import pj.io.FileIO
import pj.typeUtils.opaqueTypes.opaqueTypes.ODuration
import pj.xml.XML

import scala.xml.{Elem, Node}

final case class Agenda private(vivas: List[Viva], resources: List[Resource], duration: ODuration)

object Agenda:
  def from(vivas: List[Viva], resources: List[Resource],duration: ODuration): Agenda =
    new Agenda(vivas,resources,duration: ODuration)
  


