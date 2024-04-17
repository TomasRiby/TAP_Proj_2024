package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.{Agenda, Availability, Resource, Viva}
import pj.io.{AgendaIO, FileIO, ResourceIO}
import pj.xml.XML

import java.io.File
import scala.language.adhocExtensions
import scala.xml.Node

