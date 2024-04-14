package pj.domain

import pj.domain.Availability
import pj.typeUtils.opaqueTypes.opaqueTypes.*

final case class Teacher private(
                                  id: ID,
                                  name: Name,
                                  availability: Seq[Availability])


object Teacher:
  def from(id: ID, name: Name, availability: Seq[Availability], lista: List[String]):Result[Teacher] =
    if !lista.contains(id) then
      Right(new Teacher(id: ID, name: Name, availability: Seq[Availability]))
    else Left(DomainError.DuplicateError("There are two Teachers with the same id"))

