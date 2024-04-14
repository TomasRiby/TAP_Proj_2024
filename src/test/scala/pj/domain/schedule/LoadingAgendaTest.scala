package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.{Agenda, Availability}
import pj.io.{AgendaIO, FileIO, ResourceIO}
import pj.xml.XML

import java.io.File
import scala.language.adhocExtensions
import scala.xml.Node

class LoadingAgendaTest extends AnyFunSuite:

  test("Test all the files from the test directory"):
    val dir = "files/test/ms01/"
    val folder = new File(dir)
    val files = folder.listFiles.filter(file => file.isFile && file.getName.endsWith("in.xml")).map(file => dir + file.getName)
    files.foreach(files => println(AgendaIO.loadAgenda(files)))
    
  test("Test all the files from the assessment directory"):
    val dir = "files/assessment/ms01/"
    val folder = new File(dir)
    val files = folder.listFiles.filter(file => file.isFile && file.getName.endsWith("in.xml")).map(file => dir + file.getName)
    files.foreach(files => println(AgendaIO.loadAgenda(files)))

