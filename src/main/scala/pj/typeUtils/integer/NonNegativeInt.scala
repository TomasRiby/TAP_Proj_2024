package pj.typeUtils.integer

import pj.domain.DomainError.*
import pj.domain.Result

import scala.annotation.targetName

type NonNegativeInt = Int

object NonNegativeInt:
  def apply(n: Int): Result[NonNegativeInt] =
    if n > 0 then Right(n)
    else Left(NonNegativeIntError("Number must be greater than zero"))