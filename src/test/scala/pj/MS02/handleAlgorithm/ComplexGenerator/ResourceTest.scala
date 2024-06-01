package pj.MS02.handleAlgorithm.ComplexGenerator

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.MS02.domain.AvailabilityTest.generateAvailabilityList
import pj.MS02.handleAlgorithm.ComplexGenerator.AvailabilityGenerators.{generateAvailListForADayContainingAvail}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.IDTest.{generateExternalID, generateID, generateTeacherID}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.NameTest.generateName
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.OTimeTest.generateADay
import pj.domain.{Availability, DomainError, External, Result, Teacher}
import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.{ID, createTeacherId}

import java.time.LocalDate
import scala.annotation.tailrec
import scala.jdk.CollectionConverters.*

object ResourceTest
  extends Properties("Resource Test"):
  def generateTeacher: Gen[Teacher] =
    for
      id <- generateTeacherID
      name <- generateName
      availability <- generateAvailabilityList
    yield Teacher.from(id, name, availability)

  def generateExternal: Gen[External] =
    for
      id <- generateExternalID
      name <- generateName
      availability <- generateAvailabilityList
    yield External.from(id, name, availability)


  def generateTeacherFromID(id: ID, date: LocalDate, availability: Availability): Gen[Teacher] =
    for {
      name <- generateName
      availability <- generateAvailListForADayContainingAvail(date, availability)
    } yield Teacher.from(id, name, availability)

  def generateTeacherListFromIDs(ids: List[ID], day: LocalDate, availabilityMustInclude: Availability): Gen[List[Teacher]] =
    Gen.sequence(ids.filter(_.isTeacherId).map(id => generateTeacherFromID(id, day, availabilityMustInclude)))

  def generateExternalFromID(id: ID, date: LocalDate, availability: Availability): Gen[External] =
    for {
      name <- generateName
      availability <- generateAvailListForADayContainingAvail(date, availability)
    } yield External.from(id, name, availability)

  def generateExternalListFromIDs(ids: List[ID], day: LocalDate, availabilityMustInclude: Availability): Gen[List[External]] =
    Gen.sequence(ids.filter(_.isExternalId).map(id => generateExternalFromID(id, day, availabilityMustInclude)))


  def generateTeacherList: Gen[List[Teacher]] =
    for {
      n <- Gen.choose(0, 10)
      teachers <- Gen.listOfN(n, generateTeacher)
    } yield teachers

  
  property("Testing Teacher") = forAll(generateTeacher):
    _.isValid

  property("Testing List of Teachers") = forAll(generateTeacherList):
    _.forall(_.isValid)




    