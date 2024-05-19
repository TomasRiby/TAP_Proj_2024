package pj.MS02.domain

import org.scalacheck.Gen
import pj.domain.*
import pj.opaqueTypes.ID.{ID, createExternalId, createTeacherId}
import pj.opaqueTypes.Name.{Name, createName}
import pj.opaqueTypes.ODuration.{ODuration, createDuration}
import pj.opaqueTypes.OTime.{OTime, createTime}
import pj.opaqueTypes.Preference.{Preference, createPreference}

import java.time.LocalDateTime
import java.time.temporal.ChronoField

object Generator {

  // Gerador para ODuration no formato HH:MM:SS
  val genODuration: Gen[ODuration] = for {
    hours <- Gen.choose(0, 23)
    minutes <- Gen.choose(0, 59)
    seconds <- Gen.choose(0, 59)
    durationStr = f"$hours%02d:$minutes%02d:$seconds%02d" // Formatação da string no formato HH:MM:SS
    duration <- createDuration(durationStr).fold(
      _ => Gen.fail, // Falha no caso de erro
      Gen.const(_) // Sucesso no caso de valor válido
    )
  } yield duration

  // Gerador para OTime
  val genOTime: Gen[OTime] = for {
    hour <- Gen.choose(0, 23)
    minute <- Gen.choose(0, 59)
    second <- Gen.choose(0, 59)
    dateTime = LocalDateTime.now().withHour(hour).withMinute(minute).withSecond(second).withNano(0)
    otime <- createTime(dateTime).fold(
      _ => Gen.fail, // Falha no caso de erro
      Gen.const(_) // Sucesso no caso de valor válido
    )
  } yield otime

  // Gerador para Preference
  val genPreference: Gen[Preference] = for {
    pref <- Gen.choose(1, 5)
    preference <- createPreference(pref).fold(
      _ => Gen.fail, // Falha no caso de erro
      Gen.const(_) // Sucesso no caso de valor válido
    )
  } yield preference

  // Gerador para Availability usando OTime e Preference
  val genAvailability: Gen[Availability] = for {
    start <- genOTime
    end <- genOTime.suchThat(_.isAfter(start))
    preference <- genPreference
  } yield Availability.from(start, end, preference)

  // Gerador para Name
  val genName: Gen[Name] = for {
    nameStr <- Gen.alphaNumStr.suchThat(n => createName(n).isRight)
    name <- createName(nameStr).fold(
      _ => Gen.fail, // Falha no caso de erro
      Gen.const(_) // Sucesso no caso de valor válido
    )
  } yield name

  // Gerador para ID
  val genTeacherId: Gen[ID] = for {
    idStr <- Gen.listOfN(3, Gen.numChar).map(digits => s"T${digits.mkString}")
    id <- createTeacherId(idStr).fold(
      _ => Gen.fail, // Falha no caso de erro
      Gen.const(_) // Sucesso no caso de valor válido
    )
  } yield id

  val genExternalId: Gen[ID] = for {
    idStr <- Gen.listOfN(3, Gen.numChar).map(digits => s"E${digits.mkString}")
    id <- createExternalId(idStr).fold(
      _ => Gen.fail, // Falha no caso de erro
      Gen.const(_) // Sucesso no caso de valor válido
    )
  } yield id

  // Gerador para President, Advisor, Supervisor, CoAdvisor
  val genPresident: Gen[President] = genTeacherId.map(President.from)
  val genAdvisor: Gen[Advisor] = genTeacherId.map(Advisor.from)
  val genSupervisor: Gen[Supervisor] = genExternalId.map(Supervisor.from)
  val genCoAdvisor: Gen[CoAdvisor] = Gen.oneOf(genTeacherId.map(CoAdvisor.from), genExternalId.map(CoAdvisor.from))

  // Gerador para Role (President | Advisor | Supervisor | CoAdvisor)
  val genRole: Gen[President | Advisor | Supervisor | CoAdvisor] = Gen.oneOf(genPresident, genAdvisor, genSupervisor, genCoAdvisor)


  // Gerador para RoleLinkedWithResource
  val genRoleLinkedWithResource: Gen[RoleLinkedWithResource] = for {
    role <- genRole
    name <- genName
    availabilityList <- Gen.listOf(genAvailability)
  } yield RoleLinkedWithResource.from(role, name, availabilityList)

  // Gerador para PreViva
  val genPreViva: Gen[PreViva] = for {
    student <- genName
    title <- genName
    roleLinkedWithResourceList <- Gen.listOf(genRoleLinkedWithResource)
  } yield PreViva.from(student, title, roleLinkedWithResourceList)
}