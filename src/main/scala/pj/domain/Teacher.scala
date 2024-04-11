package pj.domain

import pj.domain.Availability
import pj.typeUtils.ResourceType

final case class Teacher private(
                          id: String,
                          name: String,
                         availability: Seq[Availability])


object Teacher:
  def from(id: String, name: String, availability: Seq[Availability]) =
    new Teacher(id: String, name: String, availability: Seq[Availability])
  
