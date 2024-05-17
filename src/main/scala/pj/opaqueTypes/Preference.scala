package pj.opaqueTypes

import pj.domain.{DomainError, Result}

import scala.util.matching.Regex


object Preference:

  opaque type Preference = Int
  private val preferencePattern: Regex = "^[1-5]$".r

  def createPreference(preference: Int): Result[Preference] =
    preference.toString match
      case preferencePattern() => Right(preference)
      case _ => Left(DomainError.InvalidPreference(s"$preference"))

  def maxPreference(p1: Preference, p2: Preference): Preference =
    if (p1 > p2) p1 else p2

  def add(first: Preference, second: Preference, third: Preference): Preference =
    first + second + third

  def toInt(p: Preference): Int = p

  // Métodos de comparação usando a conversão implícita
  extension (p: Preference)
    def >=(other: Preference): Boolean = toInt(p) >= toInt(other)
    def >(other: Preference): Boolean = toInt(p) > toInt(other)
    def <(other: Preference): Boolean = toInt(p) < toInt(other)
    def <=(other: Preference): Boolean = toInt(p) <= toInt(other)
    def toInteger: Int = toInt(p)
    def isValid: Boolean = p match
      case 1 | 2 | 3 | 4 | 5 => true
      case _ => false

  // Define an Ordering instance using the toInt conversion
  given Ordering[Preference] with
    def compare(x: Preference, y: Preference): Int = toInt(x) - toInt(y)

  given Conversion[Preference, Int] with
    def apply(op: Preference): Int = toInt(op)