package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.{Agenda, Availability, Resource, Viva}
import pj.io.{AgendaIO, FileIO, ResourceIO}
import pj.xml.XML

import java.io.File
import java.time.LocalDateTime
import scala.language.adhocExtensions
import scala.xml.Node

class MakingTheAlgorithm extends AnyFunSuite:

  test("Test a single test file from the assessment directory"):
    val dir = "files/assessment/ms01/"
    val fileName = "valid_agenda_01_in.xml"
    val filePath = dir + fileName
    val result = AgendaIO.loadAgenda(filePath)


    result match
      case Right(agenda) => println(agenda)
      case Left(error) => println(s"Erro ao carregar a agenda: $error")





