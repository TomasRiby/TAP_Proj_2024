package pj.domain

import pj.domain.Availability
import pj.typeUtils.ResourceType
import pj.typeUtils.opaqueTypes.opaqueTypes.*

final case class Teacher private(
                          id: ID,
                          name: Name,
                         availability: Seq[Availability])


object Teacher:
  def from(id: ID, name: Name, availability: Seq[Availability]) =
    new Teacher(id: ID, name: Name, availability: Seq[Availability])
  
