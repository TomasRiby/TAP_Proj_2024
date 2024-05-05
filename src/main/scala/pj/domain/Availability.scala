package pj.domain

final case class Availability(interval: Interval, preference: Preference)

object Availability:

  def from(start: DateTime, end: DateTime, preference: Preference): Result[Availability] =
    for
      interval <- Interval.from(start, end)
    yield Availability(interval, preference)
