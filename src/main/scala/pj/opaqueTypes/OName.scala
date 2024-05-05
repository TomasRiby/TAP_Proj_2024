package pj.opaqueTypes

import pj.domain.{DomainError, Result}

import scala.util.matching.Regex


object OName:
  opaque type OName = String
  private val validNamePattern: Regex = "^[a-zA-Z0-9 ]+$".r

  def createName(name: String): Result[OName] =
    if (name == "" || name.isBlank)
      Left(DomainError.WrongFormat("Name can't be blank"))
    else
      name match
        case validNamePattern() => Right(name)
        case _ => Left(DomainError.WrongFormat(s"Name '$name' is in the wrong format."))