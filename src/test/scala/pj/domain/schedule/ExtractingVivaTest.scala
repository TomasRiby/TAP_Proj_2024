package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.io.VivaIO

import scala.language.adhocExtensions

class ExtractingVivaTest extends AnyFunSuite:

  test("Extracting Viva"):
    println(VivaIO.loadViva("files/assessment/ms01/valid_agenda_06_in.xml"))





