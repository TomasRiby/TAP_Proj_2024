package pj.domain.schedule

import pj.domain.{Result, Viva}

import scala.xml.Elem

trait Schedule:
  def create(xml: Elem): Result[Elem]
