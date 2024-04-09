package pj.domain.schedule

import scala.language.adhocExtensions
import org.scalatest.funsuite.AnyFunSuite
import pj.domain.{Agenda, Availability}
import pj.io.FileIO
import pj.xml.XML

import scala.xml.Node

class ScheduleMS01Test extends AnyFunSuite:
  