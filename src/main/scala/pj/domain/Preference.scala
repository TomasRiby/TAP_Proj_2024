package pj.domain

import scala.annotation.targetName
import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex

opaque type Preference = Int

object Preference:

  private def isBetween1And5(preference: Int): Result[Unit] =
    if (preference >= 1 && preference <= 5) Right(())
    else Left(DomainError.InvalidPreference(preference.toString))

  def from(preferenceString: String): Result[Preference] =
    Try(preferenceString.toInt) match
      case Failure(exception) => Left(DomainError.InvalidPreference(s"Preference must be numeric. $preferenceString"))
      case Success(preference) =>
        for {
          _ <- isBetween1And5(preference)
        } yield preference

  extension (number: Preference)
    @targetName("Preference.to")
    def to: Int = number
    @targetName("Preference.lessOrEquals")
    def compare(other: Preference): Boolean = number <= other
    @targetName("Preference.greaterThan")
    def greaterThan(other: Preference): Boolean = number > other
    @targetName("Preference.sum")
    def sum(other: Preference): Int = number + other