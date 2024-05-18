package pj.MS02.domain

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.MS02.domain.AvailabilityTest.generateAvailabilityList
import pj.MS02.opaque.IDTest.{generateID, generateTeacherID}
import pj.MS02.opaque.NameTest.generateName
import pj.domain.Teacher

object TeacherTest
  extends Properties("Teacher Test"):
  def generateTeacher: Gen[Teacher] =
    for
      id <- generateTeacherID
      name <- generateName
      availability <- generateAvailabilityList
    yield Teacher.from(id, name, availability)

  def generateTeacherList: Gen[List[Teacher]] =
    for{
      n <- Gen.choose(0, 20)
      teachers <- Gen.listOfN(n, generateTeacher)
    } yield teachers

  property("Testing Teacher") = forAll(generateTeacher):
    _.isValid

  property("Testing List of Teachers") = forAll(generateTeacherList):
    _.forall(_.isValid)

  property("Teacher IDs are unique in list") = forAll(generateTeacherList) { teachers =>
    val ids = teachers.map(_.id)
    ids.distinct.sizeIs == ids.size
  }