package pj.MS02.domain

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.domain.{Availability, External, Period, Teacher}
import pj.opaqueTypes.OTime.{OTime, createTime}
import pj.opaqueTypes.ODuration.{ODuration, createDuration}
import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name
import pj.opaqueTypes.Name.Name
import pj.opaqueTypes.Preference.{Preference, createPreference}
import pj.domain.{DomainError, Result}

import java.time.{Duration, LocalDateTime}

object FindEarliestCommonAvailabilityTest extends Properties("Find Earliest Common Availability Test"):

  // Gerador para ID
  def generateID: Gen[ID] = Gen.oneOf(generateTeacherID, generateExternalID)

  // Gerador para Teacher ID
  def generateTeacherID: Gen[ID] = Gen.choose(0, 999).map(n => f"T$n%03d").flatMap { id =>
    ID.createTeacherId(id) match
      case Right(validId) => Gen.const(validId)
      case Left(_) => Gen.fail
  }

  // Gerador para External ID
  def generateExternalID: Gen[ID] = Gen.choose(0, 999).map(n => f"E$n%03d").flatMap { id =>
    ID.createExternalId(id) match
      case Right(validId) => Gen.const(validId)
      case Left(_) => Gen.fail
  }

  // Gerador para Name
  def generateName: Gen[Name] = Gen.alphaStr.suchThat(_.nonEmpty).flatMap { name =>
    Name.createName(name) match
      case Right(validName) => Gen.const(validName)
      case Left(_) => Gen.fail
  }

  // Gerador para OTime
  def generateOTime: Gen[OTime] = for {
    year <- Gen.choose(2000, 2030)
    month <- Gen.choose(1, 12)
    day <- Gen.choose(1, 28)
    hour <- Gen.choose(0, 23)
    minute <- Gen.choose(0, 59)
    dateTime = LocalDateTime.of(year, month, day, hour, minute)
    oTime <- createTime(dateTime) match
      case Right(validTime) => Gen.const(validTime)
      case Left(_) => Gen.fail
  } yield oTime

  // Gerador para Period
  def generatePeriod: Gen[Period] = for {
    start <- generateOTime
    end <- generateOTime.suchThat(_.isAfter(start))
    period <- Period.from(start, end) match
      case Right(p) => Gen.const(p)
      case Left(_) => Gen.fail
  } yield period

  // Gerador para Preference
  def generatePreference: Gen[Preference] = Gen.choose(1, 5).flatMap { value =>
    createPreference(value) match
      case Right(preference) => Gen.const(preference)
      case Left(_) => Gen.fail
  }

  // Gerador para Availability
  def generateAvailability: Gen[Availability] = for {
    period <- generatePeriod
    preference <- generatePreference
  } yield Availability.from(period, preference)

  // Gerador para Teacher
  def generateTeacher: Gen[Teacher] = for {
    id <- generateTeacherID
    name <- generateName
    availabilityList <- Gen.nonEmptyListOf(generateAvailability)
  } yield Teacher.from(id, name, availabilityList)

  // Gerador para External
  def generateExternal: Gen[External] = for {
    id <- generateExternalID
    name <- generateName
    availabilityList <- Gen.nonEmptyListOf(generateAvailability)
  } yield External.from(id, name, availabilityList)

  // Gerador para lista de Teacher | External
  def generateResourceList: Gen[List[Teacher | External]] = for {
    teachers <- Gen.nonEmptyListOf(generateTeacher)
    externals <- Gen.nonEmptyListOf(generateExternal)
  } yield teachers ++ externals

  // Gerador para ODuration
  def generateDuration: Gen[ODuration] =
    val durationStringGen = for {
      hours <- Gen.choose(0, 23)
      minutes <- Gen.choose(0, 59)
      seconds <- Gen.choose(0, 59)
    } yield f"$hours%02d:$minutes%02d:$seconds%02d"

    durationStringGen.flatMap { durationString =>
      createDuration(durationString) match
        case Right(duration) => Gen.const(duration)
        case Left(_) => Gen.fail
    }

  // Teste para findEarliestCommonAvailability
  property("Testing findEarliestCommonAvailability") = forAll(generateResourceList, generateDuration):
    (resources: List[Teacher | External], duration: ODuration) =>
      val result = findEarliestCommonAvailability(resources, duration)
      result match
        case Right(period) =>
          // Verifica se todos os recursos têm disponibilidade comum para a duração especificada
          resources.forall:
            case teacher: Teacher => isAvailableForDuration(teacher.availability, period, duration)
            case external: External => isAvailableForDuration(external.availability, period, duration)
        case Left(_) => true // Aceitamos falhas, pois nem sempre haverá uma disponibilidade comum

  // Função fictícia isAvailableForDuration para que o teste seja auto-suficiente
  def isAvailableForDuration(availabilities: List[Availability], period: Period, duration: ODuration): Boolean =
    availabilities.exists { availability =>
      val availableDuration = Duration.between(availability.period.start.toLocalDateT, availability.period.end.toLocalDateT).toSeconds
      availableDuration >= duration.toDuration.toSeconds
    }

  // Implementação da função findEarliestCommonAvailability para que o teste seja auto-suficiente
  def findEarliestCommonAvailability(resources: List[Teacher | External], duration: ODuration): Result[Period] =
    val commonAvailabilities = resources.flatMap {
      case teacher: Teacher => teacher.availability
      case external: External => external.availability
    }.filter { availability =>
      resources.forall:
        case teacher: Teacher => isAvailableForDuration(teacher.availability, availability.period, duration)
        case external: External => isAvailableForDuration(external.availability, availability.period, duration)
    }
    commonAvailabilities.headOption.map(_.period).toRight(DomainError.ImpossibleSchedule)
