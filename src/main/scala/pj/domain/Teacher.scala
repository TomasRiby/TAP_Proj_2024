package pj.domain

import pj.domain.Availability
import pj.io.ResourceIO
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name.Name

final case class Teacher(id: ID, name: Name, availability: List[Availability])

object Teacher {
  def from(id: ID, name: Name, availability: List[Availability]): Teacher =
    new Teacher(id, name, availability)

  extension (t: Teacher)
    def isValid: Boolean = t.id.isValid && t.name.isValid && t.availability.forall(_.isValid)
}