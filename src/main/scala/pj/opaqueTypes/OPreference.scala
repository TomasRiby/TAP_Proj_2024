package pj.opaqueTypes

import pj.domain.{DomainError, Result}

import scala.util.matching.Regex


object OPreference:

  opaque type OPreference = Int
  private val preferencePattern: Regex = "^[1-5]$".r

  def createPreference(preference: Int): Result[OPreference] =
    preference.toString match
      case preferencePattern() => Right(preference)
      case _ => Left(DomainError.InvalidPreference(s"$preference"))

  def maxPreference(p1: OPreference, p2: OPreference): OPreference =
    if (p1 > p2) p1 else p2

  def add(first: OPreference, second: OPreference, third: OPreference): OPreference =
    first + second + third

  def toInt(p: OPreference): Int = p

  // Métodos de comparação usando a conversão implícita
  extension (p: OPreference)
    def >=(other: OPreference): Boolean = toInt(p) >= toInt(other)
    def >(other: OPreference): Boolean = toInt(p) > toInt(other)
    def <(other: OPreference): Boolean = toInt(p) < toInt(other)
    def <=(other: OPreference): Boolean = toInt(p) <= toInt(other)

  // Define an Ordering instance using the toInt conversion
  given Ordering[OPreference] with
    def compare(x: OPreference, y: OPreference): Int = toInt(x) - toInt(y)