package pj.domain

import pj.io.FileIO
import pj.typeUtils.opaqueTypes.opaqueTypes.ODuration
import pj.xml.XML

import scala.xml.{Elem, Node}

final case class Agenda private(vivas: List[Viva], duration: ODuration)

object Agenda:
  def from(vivas: List[Viva], duration: ODuration): Agenda =
    new Agenda(vivas, duration)
  


