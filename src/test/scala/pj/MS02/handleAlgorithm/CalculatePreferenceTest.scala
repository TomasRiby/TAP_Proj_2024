//package pj.MS02.handleAlgorithm
//
//import org.scalacheck.Prop.forAll
//import org.scalacheck.{Gen, Properties}
//import pj.domain.{Availability, External, Teacher}
//import pj.opaqueTypes.OTime.{OTime, createTime}
//import pj.opaqueTypes.ID
//import pj.opaqueTypes.ID.ID
//import pj.opaqueTypes.Name
//import pj.opaqueTypes.Name.Name
//import pj.opaqueTypes.Preference.{Preference, createPreference}
//import pj.domain.{Result, DomainError}
//
//import java.time.LocalDateTime
//
//object CalculatePreferenceTest extends Properties("Calculate Preference Test"):
//
//  // Gerador para ID
//  def generateID: Gen[ID] = Gen.oneOf(generateTeacherID, generateExternalID)
//
//  // Gerador para Teacher ID
//  def generateTeacherID: Gen[ID] = Gen.choose(0, 999).map(n => f"T$n%03d").flatMap { id =>
//    ID.createTeacherId(id) match
//      case Right(validId) => Gen.const(validId)
//      case Left(_) => Gen.fail
//  }
//
//  // Gerador para External ID
//  def generateExternalID: Gen[ID] = Gen.choose(0, 999).map(n => f"E$n%03d").flatMap { id =>
//    ID.createExternalId(id) match
//      case Right(validId) => Gen.const(validId)
//      case Left(_) => Gen.fail
//  }
//
//  // Gerador para Name
//  def generateName: Gen[Name] = Gen.alphaStr.suchThat(_.nonEmpty).flatMap { name =>
//    Name.createName(name) match
//      case Right(validName) => Gen.const(validName)
//      case Left(_) => Gen.fail
//  }
//
//  // Gerador para OTime
//  def generateOTime: Gen[OTime] = for {
//    year <- Gen.choose(2000, 2030)
//    month <- Gen.choose(1, 12)
//    day <- Gen.choose(1, 28)
//    hour <- Gen.choose(0, 23)
//    minute <- Gen.choose(0, 59)
//    dateTime = LocalDateTime.of(year, month, day, hour, minute)
//    oTime <- createTime(dateTime) match
//      case Right(validTime) => Gen.const(validTime)
//      case Left(_) => Gen.fail
//  } yield oTime
//
//  // Gerador para Period
//  def generatePeriod: Gen[Period] = for {
//    start <- generateOTime
//    end <- generateOTime.suchThat(_.isAfter(start))
//    period <- Period.from(start, end) match
//      case Right(p) => Gen.const(p)
//      case Left(_) => Gen.fail
//  } yield period
//
//  // Gerador para Preference
//  def generatePreference: Gen[Preference] = Gen.choose(1, 5).flatMap { value =>
//    createPreference(value) match
//      case Right(preference) => Gen.const(preference)
//      case Left(_) => Gen.fail
//  }
//
//  // Gerador para Availability
//  def generateAvailability: Gen[Availability] = for {
//    period <- generatePeriod
//    preference <- generatePreference
//  } yield Availability.from(period, preference)
//
//  // Gerador para Teacher
//  def generateTeacher: Gen[Teacher] = for {
//    id <- generateTeacherID
//    name <- generateName
//    availabilityList <- Gen.listOf(generateAvailability)
//  } yield Teacher.from(id, name, availabilityList)
//
//  // Gerador para External
//  def generateExternal: Gen[External] = for {
//    id <- generateExternalID
//    name <- generateName
//    availabilityList <- Gen.listOf(generateAvailability)
//  } yield External.from(id, name, availabilityList)
//
//  // Gerador para lista de Teacher | External
//  def generateResourceList: Gen[List[Teacher | External]] = for {
//    teachers <- Gen.listOf(generateTeacher)
//    externals <- Gen.listOf(generateExternal)
//  } yield teachers ++ externals
//
//  // Teste para calculatePreference
//  property("Testing calculatePreference") = forAll(generatePeriod, generateResourceList):
//    (interval: Period, resources: List[Teacher | External]) =>
//      val result = calculatePreference(interval, resources)
//      result match
//        case Right(sum) => sum >= 0  // The sum should always be non-negative
//        case Left(_) => false  // Should not fail for generated data
//
//  // Implementação da função calculatePreference para que o teste seja auto-suficiente
//  def calculatePreference(interval: Period, resources: List[Teacher | External]): Result[Int] =
//    val preferenceSum = resources.flatMap {
//        case teacher: Teacher => teacher.availability
//        case external: External => external.availability
//      }.filter(availability => interval.isPartOf(availability.period))
//      .map(_.preference.toInteger).sum
//    Right(preferenceSum)
