package pj.domain

import pj.domain.Availability
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name.Name

final case class External(
                           id: ID,
                           name: Name,
                           availability: List[Availability]):
  override def equals(obj: Any): Boolean = obj match
    case other: External => this.id == other.id
    case _ => false

  override def hashCode(): Int = id.hashCode


object External:
  def from(id: ID, name: Name, availability: List[Availability]) =
    new External(id: ID, name: Name, availability: List[Availability])



  extension (external: External)
    def isValid: Boolean = external.id.isValid && external.name.isValid && external.availability.forall(_.isValid)
  
