package pj.domain

import pj.io.FileIO
import pj.xml.XML

import scala.xml.{Elem, Node}

final case class Agenda()

object Agenda:
  def loadTeachers(xmlData: String): Seq[Teacher] =
    Teacher.loadTeachers(xmlData)
  


