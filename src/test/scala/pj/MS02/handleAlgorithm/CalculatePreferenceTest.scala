package pj.MS02.domain

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.domain.{Availability, External, Period, Teacher}
import pj.opaqueTypes.OTime.{OTime, createTime}
import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name
import pj.opaqueTypes.Name.Name
import pj.opaqueTypes.Preference.{Preference, createPreference}
import pj.domain.{Result, DomainError}

import java.time.LocalDateTime

object CalculatePreferenceTest extends Properties("Calculate Preference Test"):

 