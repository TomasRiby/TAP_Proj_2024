package pj.domain

import pj.domain.Availability
import pj.io.ResourceIO
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name.Name

final case class Teacher private(
                                  id: ID,
                                  name: Name,
                                  availability: List[Availability]):
  override def equals(obj: Any): Boolean = obj match
    case other: Teacher => this.id == other.id
    case _ => false

  override def hashCode(): Int = id.hashCode


object Teacher:
  def from(id: ID, name: Name, availability: List[Availability]):Teacher =
    new Teacher(id: ID, name: Name, availability: List[Availability])
    
  extension(t:Teacher)
    def isValid: Boolean = t.id.isValid && t.name.isValid && t.availability.forall(_.isValid)
    
