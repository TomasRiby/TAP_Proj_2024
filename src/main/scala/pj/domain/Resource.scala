package pj.domain

import pj.typeUtils.opaqueTypes.opaqueTypes.{ID, Name}

trait Resource:
  def id: ID
  def name: Name
  def availability: List[Availability]
