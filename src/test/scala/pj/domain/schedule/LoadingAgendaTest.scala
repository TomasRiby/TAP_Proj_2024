package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.{Agenda, Availability}
import pj.io.{AgendaIO, FileIO, ResourceIO}
import pj.xml.XML

import scala.language.adhocExtensions
import scala.xml.Node

class LoadingAgendaTest extends AnyFunSuite:

  test("Testy"):
    println(AgendaIO.loadAgenda("files/assessment/ms01/invalid_preference_in.xml"))

