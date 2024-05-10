package pj.opaqueTypes

import pj.domain.{DomainError, Result}

import scala.util.matching.Regex


object Name:
  opaque type Name = String
  private val validNamePattern: Regex = "^[a-zA-Z0-9 ]+$".r

  def createName(name: String): Result[Name] =
    if (name == "" || name.isBlank)
      Left(DomainError.WrongFormat("Name can't be blank"))
    else
      name match
        case validNamePattern() => Right(name)
        case _ => Left(DomainError.WrongFormat(s"Name '$name' is in the wrong format."))