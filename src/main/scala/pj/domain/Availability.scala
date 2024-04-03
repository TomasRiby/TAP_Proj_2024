package pj.domain

import pj.xml.XML

import scala.xml.Node


final case class Availability(
                               start: String,
                               end: String,
                               preference: String
                             )




