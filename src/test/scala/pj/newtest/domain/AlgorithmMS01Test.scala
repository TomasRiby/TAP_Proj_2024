package pj.newtest.domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.OptionValues
import org.scalatest.EitherValues
import pj.domain.*
import pj.opaqueTypes.{ID, ODuration, OTime, Preference}
import pj.opaqueTypes.ID.ID
import pj.opaqueTypes.Name
import pj.opaqueTypes.Name.Name
import pj.opaqueTypes.ODuration.ODuration
import pj.opaqueTypes.OTime.OTime
import pj.opaqueTypes.Preference.Preference

import java.time.LocalDateTime
import scala.collection.immutable.HashSet

class AlgorithmMS01Test extends AnyFlatSpec with Matchers with OptionValues with EitherValues:

  def createValidPreViva(studentName: String, title: String, presidentId: String, advisorId: String, supervisorId: String): PreViva =
    val student = Name.createName(studentName).getOrElse(fail("Failed to create Name"))
    val thesisTitle = Name.createName(title).getOrElse(fail("Failed to create Name"))
    val president = President.from(ID.createTeacherId(presidentId).getOrElse(fail("Failed to create ID")))
    val advisor = Advisor.from(ID.createTeacherId(advisorId).getOrElse(fail("Failed to create ID")))
    val supervisor = Supervisor.from(ID.createExternalId(supervisorId).getOrElse(fail("Failed to create External ID")))

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
      RoleLinkedWithResource.from(supervisor, Name.createName("Supervisor Name").getOrElse(fail("Failed to create Name")), external1Availability)
    )
    PreViva.from(student, thesisTitle, roleList)

  def createDuration(duration: String): ODuration =
    ODuration.createDuration(duration).getOrElse(fail("Failed to create ODuration"))

  "algorithmFCFS" should "schedule vivas correctly for valid inputs" in:
    val validPreViva1 = createValidPreViva("Student 001", "Title 1", "T001", "T002", "E001")
    val validPreViva2 = createValidPreViva("Student 002", "Title 2", "T002", "T001", "E001")
    val duration = createDuration("01:00:00")
    val preVivaList = Seq(validPreViva1, validPreViva2)

    val result = AlgorithmMS01.algorithmFCFS(preVivaList, duration)

    result shouldBe a[Right[?, ?]]
    result.map { scheduleOut =>
      scheduleOut.posVivas should have length 2
      val sortedVivas = scheduleOut.posVivas.sortBy(_.student)
      sortedVivas.headOption.value.student should be("Student 001")
      sortedVivas.lift(1).value.student should be("Student 002")
    }

  it should "return ImpossibleSchedule for conflicting vivas" in:
    val validPreViva1 = createValidPreViva("Student 001", "Title 1", "T001", "T002", "E001")
    val validPreViva2 = createValidPreViva("Student 002", "Title 2", "T001", "T002", "E001") // Conflita com validPreViva1
    val duration = createDuration("01:00:00")
    val conflictingPreVivaList = Seq(validPreViva1, validPreViva2, validPreViva1, validPreViva2)

    val result = AlgorithmMS01.algorithmFCFS(conflictingPreVivaList, duration)

    result shouldBe a[Left[?, ?]]
    result.left.value should be(DomainError.ImpossibleSchedule)

  it should "handle empty preVivaList" in:
    val duration = createDuration("01:00:00")
    val emptyPreVivaList = Seq.empty[PreViva]

    val result = AlgorithmMS01.algorithmFCFS(emptyPreVivaList, duration)

    result shouldBe a[Right[?, ?]]
    result.map { scheduleOut =>
      scheduleOut.posVivas shouldBe empty
    }

  it should "handle single preViva" in:
    val validPreViva1 = createValidPreViva("Student 001", "Title 1", "T001", "T002", "E001")
    val duration = createDuration("01:00:00")
    val singlePreVivaList = Seq(validPreViva1)

    val result = AlgorithmMS01.algorithmFCFS(singlePreVivaList, duration)

    result shouldBe a[Right[?, ?]]
    result.map { scheduleOut =>
      scheduleOut.posVivas should have length 1
      scheduleOut.posVivas.headOption.value.student should be("Student 001")
    }
