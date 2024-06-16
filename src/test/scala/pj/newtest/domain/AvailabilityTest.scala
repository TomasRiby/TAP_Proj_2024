package pj.newtest.domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.OptionValues
import pj.domain.{Availability, DomainError, PreViva, RoleLinkedWithResource, Teacher, External, President, Advisor, Supervisor, CoAdvisor}
import pj.opaqueTypes.{OTime, Preference, ODuration, ID, Name}
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference.Preference
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name.Name

import java.time.LocalDateTime
import scala.collection.immutable.HashSet

class AvailabilityTest extends AnyFlatSpec with Matchers with OptionValues:

  def createDuration(duration: String): ODuration =
    ODuration.createDuration(duration).getOrElse(fail("Failed to create ODuration"))

  def createValidPreViva(studentName: String, title: String, presidentId: String, advisorId: String, supervisorId: String, coAdvisorId: String): PreViva =
    val student = Name.createName(studentName).getOrElse(fail("Failed to create Name"))
    val thesisTitle = Name.createName(title).getOrElse(fail("Failed to create Name"))
    val president = President.from(ID.createTeacherId(presidentId).getOrElse(fail("Failed to create ID")))
    val advisor = Advisor.from(ID.createTeacherId(advisorId).getOrElse(fail("Failed to create ID")))
    val supervisor = Supervisor.from(ID.createExternalId(supervisorId).getOrElse(fail("Failed to create External ID")))
    val coAdvisor = CoAdvisor.from(ID.createTeacherId(coAdvisorId).getOrElse(fail("Failed to create ID")))

    val teacher1Availability = List(
      Availability.from(
        OTime.createTime("2024-05-30T09:30:00").getOrElse(fail("Failed to create OTime")),
        OTime.createTime("2024-05-30T12:30:00").getOrElse(fail("Failed to create OTime")),
        Preference.createPreference(5).getOrElse(fail("Failed to create Preference"))
      ),
      Availability.from(
        OTime.createTime("2024-05-30T13:30:00").getOrElse(fail("Failed to create OTime")),
        OTime.createTime("2024-05-30T16:30:00").getOrElse(fail("Failed to create OTime")),
        Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))
      )
    )

    val teacher2Availability = List(
      Availability.from(
        OTime.createTime("2024-05-30T10:30:00").getOrElse(fail("Failed to create OTime")),
        OTime.createTime("2024-05-30T11:30:00").getOrElse(fail("Failed to create OTime")),
        Preference.createPreference(5).getOrElse(fail("Failed to create Preference"))
      ),
      Availability.from(
        OTime.createTime("2024-05-30T14:30:00").getOrElse(fail("Failed to create OTime")),
        OTime.createTime("2024-05-30T17:00:00").getOrElse(fail("Failed to create OTime")),
        Preference.createPreference(5).getOrElse(fail("Failed to create Preference"))
      )
    )

    val external1Availability = List(
      Availability.from(
        OTime.createTime("2024-05-30T10:00:00").getOrElse(fail("Failed to create OTime")),
        OTime.createTime("2024-05-30T13:30:00").getOrElse(fail("Failed to create OTime")),
        Preference.createPreference(2).getOrElse(fail("Failed to create Preference"))
      ),
      Availability.from(
        OTime.createTime("2024-05-30T15:30:00").getOrElse(fail("Failed to create OTime")),
        OTime.createTime("2024-05-30T18:00:00").getOrElse(fail("Failed to create OTime")),
        Preference.createPreference(5).getOrElse(fail("Failed to create Preference"))
      )
    )

    val roleList = List(
      RoleLinkedWithResource.from(president, Name.createName("President Name").getOrElse(fail("Failed to create Name")), teacher1Availability),
      RoleLinkedWithResource.from(advisor, Name.createName("Advisor Name").getOrElse(fail("Failed to create Name")), teacher2Availability),
      RoleLinkedWithResource.from(supervisor, Name.createName("Supervisor Name").getOrElse(fail("Failed to create Name")), external1Availability),
      RoleLinkedWithResource.from(coAdvisor, Name.createName("CoAdvisor Name").getOrElse(fail("Failed to create Name")), teacher1Availability)
    )
    PreViva.from(student, thesisTitle, roleList)

  def createSimpleValidPreViva(studentName: String, title: String, presidentId: String, advisorId: String, supervisorId: String, coAdvisorId: String): PreViva =
    val student = Name.createName(studentName).getOrElse(fail("Failed to create Name"))
    val thesisTitle = Name.createName(title).getOrElse(fail("Failed to create Name"))
    val president = President.from(ID.createTeacherId(presidentId).getOrElse(fail("Failed to create ID")))
    val advisor = Advisor.from(ID.createTeacherId(advisorId).getOrElse(fail("Failed to create ID")))
    val supervisor = Supervisor.from(ID.createExternalId(supervisorId).getOrElse(fail("Failed to create External ID")))
    val coAdvisor = CoAdvisor.from(ID.createTeacherId(coAdvisorId).getOrElse(fail("Failed to create ID")))

    val availability = List(
      Availability.from(
        OTime.createTime("2024-05-30T09:30:00").getOrElse(fail("Failed to create OTime")),
        OTime.createTime("2024-05-30T12:30:00").getOrElse(fail("Failed to create OTime")),
        Preference.createPreference(5).getOrElse(fail("Failed to create Preference"))
      )
    )

    val roleList = List(
      RoleLinkedWithResource.from(president, Name.createName("President Name").getOrElse(fail("Failed to create Name")), availability),
      RoleLinkedWithResource.from(advisor, Name.createName("Advisor Name").getOrElse(fail("Failed to create Name")), availability),
      RoleLinkedWithResource.from(supervisor, Name.createName("Supervisor Name").getOrElse(fail("Failed to create Name")), availability),
      RoleLinkedWithResource.from(coAdvisor, Name.createName("CoAdvisor Name").getOrElse(fail("Failed to create Name")), availability)
    )
    PreViva.from(student, thesisTitle, roleList)

  "fromCheck" should "return Right for valid start and end times" in :
    val start = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val end = OTime.createTime("2023-06-14T12:15:30").getOrElse(fail("Failed to create OTime"))
    val preference = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val result = Availability.fromCheck(start, end, preference)

    result should be(Right(Availability.from(start, end, preference)))

  it should "return Left for invalid start and end times" in :
    val start = OTime.createTime("2023-06-14T12:15:30").getOrElse(fail("Failed to create OTime"))
    val end = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val preference = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val result = Availability.fromCheck(start, end, preference)

    result should be(Left(DomainError.WrongFormat("End time must be after start time")))

  "isValid" should "return true for valid Availability" in :
    val start = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val end = OTime.createTime("2023-06-14T12:15:30").getOrElse(fail("Failed to create OTime"))
    val preference = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val availability = Availability.from(start, end, preference)

    availability.isValid should be(true)

  it should "return false for invalid Availability with start time after end time" in :
    val start = OTime.createTime("2023-06-14T12:15:30").getOrElse(fail("Failed to create OTime"))
    val end = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val preference = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val availability = Availability.from(start, end, preference)

    availability.isValid should be(false)

  "intersects" should "return true for overlapping availabilities" in :
    val start1 = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val end1 = OTime.createTime("2023-06-14T12:15:30").getOrElse(fail("Failed to create OTime"))
    val preference1 = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val start2 = OTime.createTime("2023-06-14T11:00:00").getOrElse(fail("Failed to create OTime"))
    val end2 = OTime.createTime("2023-06-14T13:00:00").getOrElse(fail("Failed to create OTime"))
    val preference2 = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val availability1 = Availability.from(start1, end1, preference1)
    val availability2 = Availability.from(start2, end2, preference2)

    Availability.intersects(availability1, availability2) should be(true)

  it should "return false for non-overlapping availabilities" in :
    val start1 = OTime.createTime("2023-06-14T10:15:30").getOrElse(fail("Failed to create OTime"))
    val end1 = OTime.createTime("2023-06-14T11:15:30").getOrElse(fail("Failed to create OTime"))
    val preference1 = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val start2 = OTime.createTime("2023-06-14T12:00:00").getOrElse(fail("Failed to create OTime"))
    val end2 = OTime.createTime("2023-06-14T13:00:00").getOrElse(fail("Failed to create OTime"))
    val preference2 = Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))

    val availability1 = Availability.from(start1, end1, preference1)
    val availability2 = Availability.from(start2, end2, preference2)

    Availability.intersects(availability1, availability2) should be(false)

  "chooseFirstPossibleAvailability" should "return the first available slot" in:
    val duration = createDuration("01:00:00")

    val start1 = OTime.createTime("2024-05-30T09:00:00").getOrElse(fail("Failed to create OTime"))
    val end1 = OTime.createTime("2024-05-30T10:30:00").getOrElse(fail("Failed to create OTime"))
    val start2 = OTime.createTime("2024-05-30T11:00:00").getOrElse(fail("Failed to create OTime"))
    val end2 = OTime.createTime("2024-05-30T12:00:00").getOrElse(fail("Failed to create OTime"))

    val availabilities = List(
      Availability.from(start1, end1, Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))),
      Availability.from(start2, end2, Preference.createPreference(5).getOrElse(fail("Failed to create Preference")))
    )

    val usedSlots = List.empty[(HashSet[ID], Availability)]
    val newIds = HashSet(ID.createTeacherId("T001").getOrElse(fail("Failed to create ID")))

    val result = Availability.chooseFirstPossibleAvailability(availabilities, duration, usedSlots, newIds)

    result._1 shouldBe defined
    result._1.value._1 should be(start1.toLocalDateTime)
    result._1.value._2 should be(start1.toLocalDateTime.plus(duration.toDuration))
    result._1.value._3 should be(3)

  "chooseFirstPossibleAvailabilitiesSlot" should "return the first available slot" in:
    val duration = createDuration("01:00:00")

    val start1 = OTime.createTime("2024-05-30T09:00:00").getOrElse(fail("Failed to create OTime"))
    val end1 = OTime.createTime("2024-05-30T10:30:00").getOrElse(fail("Failed to create OTime"))
    val start2 = OTime.createTime("2024-05-30T11:00:00").getOrElse(fail("Failed to create OTime"))
    val end2 = OTime.createTime("2024-05-30T12:00:00").getOrElse(fail("Failed to create OTime"))

    val availabilities = List(
      Availability.from(start1, end1, Preference.createPreference(3).getOrElse(fail("Failed to create Preference"))),
      Availability.from(start2, end2, Preference.createPreference(5).getOrElse(fail("Failed to create Preference")))
    )

    val usedSlots = List.empty[Availability]

    val result = Availability.chooseFirstPossibleAvailabilitiesSlot(availabilities, duration, usedSlots)

    result._1 shouldBe defined
    result._1.value._1 should be(start1.toLocalDateTime)
    result._1.value._2 should be(start1.toLocalDateTime.plus(duration.toDuration))
    result._1.value._3 should be(3)

  it should "return None when all availabilities are used" in:
    val duration = createDuration("01:00:00")

    val start1 = OTime.createTime("2024-05-30T09:00:00").getOrElse(fail("Failed to create OTime"))
    val end1 = OTime.createTime("2024-05-30T10:00:00").getOrElse(fail("Failed to create OTime"))

    val availabilities = List(
      Availability.from(start1, end1, Preference.createPreference(3).getOrElse(fail("Failed to create Preference")))
    )

    val usedAvailability = Availability.from(start1, end1, Preference.createPreference(3).getOrElse(fail("Failed to create Preference")))

    val usedSlots = List(usedAvailability)

    val result = Availability.chooseFirstPossibleAvailabilitiesSlot(availabilities, duration, usedSlots)

    result._1 shouldBe None


