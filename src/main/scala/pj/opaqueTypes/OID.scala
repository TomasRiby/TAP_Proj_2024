package pj.opaqueTypes

import pj.domain.{DomainError, External, OExternal, OTeacher, Result, Teacher}

import scala.util.matching.Regex

object OID:
  opaque type OID = String
  private val teacherIdPattern: Regex = "^T[0-9]{3}$".r
  private val externalIdPattern: Regex = "^E[0-9]{3}$".r

  def createRegularId(id: String): Result[OID] =
    id match
      case teacherIdPattern() => Right(id)
      case externalIdPattern() => Right(id)
      case _ => Left(DomainError.WrongFormat(s"ID '$id' is in incorrect format"))

  def verifyId(resourceList: List[OTeacher | OExternal]): Result[Boolean] =
    val idList = resourceList.map:
      case teacher: OTeacher => teacher.id
      case external: OExternal => external.id
    val idSet = idList.toSet
    if idList.size != idSet.size then
      Left(DomainError.DuplicateError(s"Duplicate IDs found in the $idList"))
    else
      Right(true)


  def createTeacherId(id: String): Result[OID] =
    id match
      case teacherIdPattern() => Right(id)
      case _ => Left(DomainError.WrongFormat(s"Teacher´s ID '$id' should be in the *T001* format"))

  def createExternalId(id: String): Result[OID] =
    id match
      case externalIdPattern() => Right(id)
      case _ => Left(DomainError.WrongFormat(s"External´s ID '$id' should be in the *E001* format"))