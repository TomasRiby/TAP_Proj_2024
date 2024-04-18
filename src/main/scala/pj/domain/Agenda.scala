package pj.domain

import pj.io.FileIO
import pj.typeUtils.opaqueTypes.opaqueTypes.ODuration
import pj.xml.XML

import scala.xml.{Elem, Node}

final case class Agenda private(vivas: Seq[Viva], resources: Resource, duration: ODuration)

object Agenda:
  def from(vivas: Seq[Viva], resources: Resource,duration: ODuration): Agenda =
    new Agenda(vivas,resources,duration: ODuration)
  


