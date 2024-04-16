package pj.domain

import pj.io.FileIO
import pj.xml.XML

import scala.xml.{Elem, Node}

final case class Agenda private(vivas: Seq[Viva], resources: Resource, duration: String)

object Agenda:
  def from(vivas: Seq[Viva], resources: Resource,duration: String): Agenda =
    new Agenda(vivas,resources,duration: String)
  


