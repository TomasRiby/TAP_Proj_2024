package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.{Agenda, Availability, Resource, Viva}
import pj.io.{AgendaIO, FileIO, ResourceIO}
import pj.typeUtils.opaqueTypes.opaqueTypes.ID
import pj.xml.XML

import java.io.File
import java.time.LocalDateTime
import scala.language.adhocExtensions
import scala.xml.Node
