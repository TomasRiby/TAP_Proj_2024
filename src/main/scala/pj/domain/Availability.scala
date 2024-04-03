package pj.domain

import pj.xml.XML

import scala.xml.Node


final case class Availability(
                               start: String,
                               end: String,
                               preference: String
                             )

object Availability:
  //Abaixo definimos as funções
  def extractAvailability(availabilityNode: Node): Seq[Availability] =
    (availabilityNode \ "availability").flatMap { node =>
      for {
        start <- XML.fromAttribute(node, "start").toOption
        end <- XML.fromAttribute(node, "end").toOption
        preference <- XML.fromAttribute(node, "preference").toOption
      } yield Availability(start, end, preference)
    }



