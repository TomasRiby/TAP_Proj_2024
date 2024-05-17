package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.*
import pj.io.{AgendaIO, FileIO, ResourceIO}
import pj.xml.XML

import java.io.File
import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec
import scala.language.adhocExtensions
import scala.xml.Node

class Test extends AnyFunSuite:

  test("God Saver"):
    val dir = "files/assessment/ms01/"
    val fileName = "invalid_agenda_01_in.xml"
    val filePath = dir + fileName
    val result = for {
      fileLoaded <- FileIO.load(filePath)
      result <- ScheduleMS01.create(fileLoaded)
    } yield result
    println(result)

  test("test"):
    val dir = "files/test/ms01/"
    val folder = new File(dir)
    val files = folder.listFiles.filter(file => file.isFile && file.getName.endsWith("in.xml")).map(file => dir + file.getName)
    files.foreach(files => println(AgendaIO.loadAgenda(files)))

  test("assessment"):
    val dir = "files/assessment/ms01/"
    val folder = new File(dir)
    val files = folder.listFiles.filter(file => file.isFile && file.getName.endsWith("in.xml")).map(file => dir + file.getName)
    files.foreach(files =>
      val result = for {
        fileLoaded <- FileIO.load(files)
        result <- ScheduleMS01.create(fileLoaded)
      } yield result
    )

  test("asasasas"):
    val dir = "files/assessment/ms01/"
    val fileName = "valid_agenda_26_in.xml"
    val filePath = dir + fileName
    val result = AgendaIO.loadAgenda(filePath)


    result match
      case Right(agenda) => println(agenda)
      case Left(error) => println(s"Erro ao carregar a agenda: $error")


