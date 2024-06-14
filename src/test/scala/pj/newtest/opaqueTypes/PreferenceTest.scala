package pj.newtest.opaqueTypes

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pj.opaqueTypes.Preference
import pj.domain.{DomainError, Result}

class PreferenceTest extends AnyFlatSpec with Matchers:

  "createPreference" should "return Right for valid preference" in:
    val result = Preference.createPreference(3)
    result should be (Right(3))

  it should "return Left for invalid preference" in:
    val result = Preference.createPreference(6)
    result should be (Left(DomainError.InvalidPreference("6")))

  it should "return Left for negative preference" in:
    val result = Preference.createPreference(-1)
    result should be (Left(DomainError.InvalidPreference("-1")))

  "fromMoreThan5" should "return preference even if it is more than 5" in:
    val preference = Preference.fromMoreThan5(10)
    preference should be (10)

  "maxPreference" should "return the maximum of two preferences" in:
    val p1 = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))
    val p2 = Preference.createPreference(5).getOrElse(fail("Failed to create Preference"))
    val result = Preference.maxPreference(p1, p2)
    result should be (5)

  "add" should "correctly add three preferences" in:
    val p1 = Preference.createPreference(1).getOrElse(fail("Failed to create Preference"))
    val p2 = Preference.createPreference(2).getOrElse(fail("Failed to create Preference"))
    val p3 = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))
    val result = Preference.add(p1, p2, p3)
    result should be (6)

  "toInt" should "correctly convert Preference to Int" in:
    val preference = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))
    val result = Preference.toInt(preference)
    result should be (3)

  "Preference extension methods" should "correctly compare preferences" in:
    val p1 = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))
    val p2 = Preference.createPreference(5).getOrElse(fail("Failed to create Preference"))

    (p1 >= p2) should be (false)
    (p1 > p2) should be (false)
    (p1 < p2) should be (true)
    (p1 <= p2) should be (true)

  it should "correctly identify valid preference" in:
    val preference = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))
    preference.isValid should be (true)

  it should "correctly identify invalid preference" in:
    val preference = Preference.fromMoreThan5(6)
    preference.isValid should be (false)

  it should "correctly convert Preference to Integer" in:
    val preference = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))
    preference.toInteger should be (3)
