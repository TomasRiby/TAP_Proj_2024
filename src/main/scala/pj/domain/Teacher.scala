package pj.domain

import pj.io.FileIO

import scala.xml.Node
import pj.xml.XML

final case class Teacher(
                          id: String,
                          name: String,
                          availability: Seq[Availability]
                        )







