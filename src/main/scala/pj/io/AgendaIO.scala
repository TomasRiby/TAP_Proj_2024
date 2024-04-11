package pj.io

import pj.domain.{External, Result, Teacher, Viva}


object AgendaIO:
  def loadAgenda(xmlPath: String): Result[Seq[Teacher | External | Viva]] =
    for {
      viva <- VivaIO.loadViva(xmlPath)
      resources <- ResourceIO.loadResources(xmlPath)
    } yield viva ++ resources


