package pj.domain

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.opaqueTypes.Name.Name
import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.ID
import pj.MS02.domain.AvailabilityTest.{generateAvailability, generateAvailabilityList, generateNonOverlappingAvailabilityList}
import pj.MS02.domain.ResourceTest.{generateExternal, generateTeacher}
import pj.MS02.domain.VivaTest.{generateAdvisor, generateCoAdvisor, generatePresident, generateSupervisor, generateViva}
import pj.MS02.opaque.IDTest.generateTeacherID
import pj.MS02.opaque.NameTest.generateName

object PreVivaTest extends Properties("PreViva Test"):

  // Assuming getId method is defined for President, Advisor, Supervisor, and CoAdvisor
  extension (role: President | Advisor | Supervisor | CoAdvisor)
    def getId: ID = role match
      case p: President => p.id
      case a: Advisor => a.id
      case s: Supervisor => s.id
      case c: CoAdvisor => c.id

  property("linkVivaWithResource should correctly link viva with resources") = forAll(
    Gen.listOfN(10, generateTeacherID),
    Gen.listOf(generateTeacher),
    Gen.listOf(generateExternal)
  ) { (idList, teachers, externals) =>
    val vivaOpt = generateViva(idList).sample

    vivaOpt.fold(false) { viva =>
      val preViva = PreViva.linkVivaWithResource(viva, teachers, externals)

      // Check that the student and title are correctly transferred
      val studentCorrect = preViva.student == viva.student
      val titleCorrect = preViva.title == viva.title

      // Check that the roles are correctly linked with resources
      val presidentCorrect = preViva.roleLinkedWithResourceList.exists(rlr => rlr.role == viva.president && teachers.exists(_.id == rlr.role.getId))
      val advisorCorrect = preViva.roleLinkedWithResourceList.exists(rlr => rlr.role == viva.advisor && teachers.exists(_.id == rlr.role.getId))

      val coAdvisorCorrect = viva.coAdvisor.forall { coAdvisor =>
        preViva.roleLinkedWithResourceList.exists(rlr => rlr.role == coAdvisor && (teachers.exists(_.id == coAdvisor.getId) || externals.exists(_.id == coAdvisor.getId)))
      }

      val supervisorCorrect = viva.supervisor.forall { supervisor =>
        preViva.roleLinkedWithResourceList.exists(rlr => rlr.role == supervisor && externals.exists(_.id == supervisor.getId))
      }

      studentCorrect && titleCorrect && presidentCorrect && advisorCorrect && coAdvisorCorrect && supervisorCorrect
    }
  }
