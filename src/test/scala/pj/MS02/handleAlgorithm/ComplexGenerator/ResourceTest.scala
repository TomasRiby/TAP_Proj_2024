package pj.MS02.handleAlgorithm.ComplexGenerator

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import pj.MS02.domain.AvailabilityTest.generateAvailabilityList
import pj.MS02.handleAlgorithm.ComplexGenerator.AvailabilityGenerators.generateAvailabilityListForADay
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.IDTest.{generateExternalID, generateID, generateTeacherID}
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.NameTest.generateName
import pj.MS02.handleAlgorithm.ComplexGenerator.opaque.OTimeTest.generateADay
import pj.domain.{DomainError, External, Result, Teacher}
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


  def generateTeacherFromID(id: ID, date: LocalDate): Gen[Teacher] =
    for {
      name <- generateName
      availability <- generateAvailabilityListForADay(date)
    } yield Teacher.from(id, name, availability)

  def generateTeacherListFromIDs(ids: List[ID], day: LocalDate): Gen[List[Teacher]] =
    Gen.sequence(ids.filter(_.isTeacherId).map(id => generateTeacherFromID(id, day)))

  def generateExternalFromID(id: ID, date: LocalDate): Gen[External] =
    for {
      name <- generateName
      availability <- generateAvailabilityListForADay(date)
    } yield External.from(id, name, availability)

  def generateExternalListFromIDs(ids: List[ID], day: LocalDate): Gen[List[External]] =
    Gen.sequence(ids.filter(_.isExternalId).map(id => generateExternalFromID(id, day)))


  def generateTeacherList: Gen[List[Teacher]] =
    for {
      n <- Gen.choose(0, 10)
      teachers <- Gen.listOfN(n, generateTeacher)
    } yield teachers

  property("Testing Teacher") = forAll(generateTeacher):
    _.isValid

  property("Testing List of Teachers") = forAll(generateTeacherList):
    _.forall(_.isValid)


  def generateExternal: Gen[External] =
    for
      id <- generateExternalID
      name <- generateName
      availability <- generateAvailabilityList
    yield External.from(id, name, availability)

  val res: Result[(List[ID], LocalDate)] = for {
    id1 <- ID.createTeacherId("T001")
    day = generateADay.sample.getOrElse(LocalDate.now())
    id2 <- ID.createTeacherId("T002")
    id3 <- ID.createTeacherId("T003")
    id4 <- ID.createTeacherId("T004")
    id5 <- ID.createTeacherId("T005")
    id6 <- ID.createTeacherId("T006")
  } yield (List(id1, id2, id3, id4, id5), day)

  res match
    case Right((ids, day)) => property("Testing if Teacher from List of IDs and a Day can be created Successfully") = forAll(generateTeacherListFromIDs(ids, day)) { res =>
      res.forall(_.isValid)
    }
    case Left(value) => println(value)
  property("Testing External") = forAll(generateExternal):
    _.isValid

    