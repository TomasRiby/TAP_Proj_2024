package pj.MS02.opaque

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.opaqueTypes.{Name, OTime}
import pj.opaqueTypes.OTime.OTime

import java.time.{Month, Year}

object OTimeTest extends Properties("OTimeTest"):
  /** Generator for valid `Time` instances in ISO-8601 format `yyyy-MM-dd'T'HH:mm:ss` */
  def generateTime: Gen[OTime] =
    // Generate year, month, and day ensuring validity for each month
    val yearGen = Gen.choose(1900, 2100)
    val monthGen = Gen.choose(1, 12)

    def dayGen(year: Int, month: Int): Gen[Int] =
      val daysInMonth = Month.of(month).length(Year.isLeap(year))
      Gen.choose(1, daysInMonth)

    // Generate hours, minutes, and seconds
    val hourGen = Gen.choose(0, 23)
    val minuteGen = Gen.choose(0, 59)
    val secondGen = Gen.choose(0, 59)

    for {
      year <- yearGen
      month <- monthGen
      day <- dayGen(year, month)
      hour <- hourGen
      minute <- minuteGen
      second <- secondGen
      dateTimeStr = f"$year%04d-$month%02d-$day%02dT$hour%02d:$minute%02d:$second%02d"
      time <- OTime.createTime(dateTimeStr) match
        case Right(validTime) => Gen.const(validTime)
        case Left(_) => Gen.fail
    } yield time

  property("createTime only allows valid ISO-8601 times") = forAll(generateTime):
    _.isValid
  