package pj.domain

import pj.domain.Availability
import pj.io.ResourceIO
import pj.typeUtils.opaqueTypes.opaqueTypes.*

final case class Teacher private(
                                  id: ID,
                                  name: Name,
                                  availability: List[Availability])


object Teacher:
  def from(id: ID, name: Name, availability: List[Availability]):Teacher =
    new Teacher(id: ID, name: Name, availability: List[Availability])
    
