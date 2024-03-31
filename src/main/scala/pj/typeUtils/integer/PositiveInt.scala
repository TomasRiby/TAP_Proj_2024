package pj.typeUtils.integer

import pj.domain.DomainError.*
import pj.domain.Result

import scala.annotation.targetName

type PositiveInt = Int

object PositiveInt:
  def apply(n: Int): Result[PositiveInt] =
    if n >= 0 then Right(n)
    else Left(PositiveIntError("Number must be positive"))