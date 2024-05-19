package pj.MS02.handleAlgorithm.ComplexGenerator.opaque

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.opaqueTypes.ODuration
import pj.opaqueTypes.ODuration.ODuration

object ODurationTest extends Properties("ODurationTest"):

  def generateDuration: Gen[ODuration] =
    for {
      hours <- Gen.choose(1, 2) // 1 to 3 hours
      minutes <- Gen.choose(0, 59)
      seconds <- Gen.choose(0, 59)
      durationStr = f"$hours%02d:$minutes%02d:$seconds%02d"
      duration <- ODuration.createDuration(durationStr) match
        case Left(_) => Gen.fail
        case Right(validDuration) => Gen.const(validDuration)
    } yield duration

  property("Duration generation is working") = forAll(generateDuration):
    duration => duration.toDuration.toHours > 0 && duration.toDuration.toHours <= 2

