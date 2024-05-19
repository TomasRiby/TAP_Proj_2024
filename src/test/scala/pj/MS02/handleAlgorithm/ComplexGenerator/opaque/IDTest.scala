package pj.MS02.handleAlgorithm.ComplexGenerator.opaque

import org.scalacheck.{Gen, Properties}
import org.scalacheck.Prop.forAll
import pj.opaqueTypes.ID
import pj.opaqueTypes.ID.ID


object IDTest extends Properties("ID"):
  def generateID: Gen[ID] =
    for {
      prefix <- Gen.oneOf("T", "E")
      number <- Gen.chooseNum(1, 999)
      resID = ID.createRegularId(f"$prefix$number%03d")
      generatedID <- resID match
        case Left(_) => Gen.fail
        case Right(validID) => Gen.const(validID)

    } yield generatedID

  def generateTeacherID: Gen[ID] =
    for {
      number <- Gen.chooseNum(1, 999)
      resID = ID.createTeacherId(f"T$number%03d")
      generatedID <- resID match
        case Left(_) => Gen.fail
        case Right(validID) => Gen.const(validID)

    } yield generatedID

  def generateExternalID: Gen[ID] =
    for {
      number <- Gen.chooseNum(1, 999)
      resID = ID.createExternalId(f"E$number%03d")
      generatedID <- resID match
        case Left(_) => Gen.fail
        case Right(validID) => Gen.const(validID)

    } yield generatedID

  def generateUniqueIDs(maxNumber: Int): Gen[List[ID]] =
    for {
      teacherIds <- Gen.listOfN(maxNumber / 2, generateTeacherID)
      externalIds <- Gen.listOfN(maxNumber / 2, generateExternalID)
      res = teacherIds.distinct ++ externalIds.distinct
    } yield res

  def generateTeacherIdThatsNotOnList(ids: List[ID]): Gen[ID] =
    generateTeacherID.retryUntil(id => !ids.contains(id))

  def generateExternalIdThatsNotOnList(ids: List[ID]): Gen[ID] =
    generateExternalID.retryUntil(id => !ids.contains(id))

  def generateIDThatsNotOnList(ids: List[ID]): Gen[ID] =
    generateID.retryUntil(id => !ids.contains(id))

  property("IDs are in the correct format") = forAll(generateID):
    _.isValid

  property("Teacher IDs are in the correct format") = forAll(generateTeacherID):
    _.isTeacherId

  property("External IDs are in the correct format") = forAll(generateExternalID):
    _.isExternalId

  property("IDs are unique") = forAll(generateUniqueIDs(10)) { ids =>
    ids.distinct.sizeIs == ids.size
  }

  property("Teacher IDs are unique") = forAll(generateUniqueIDs(20)) { ids =>
    ids.filter(_.isTeacherId).distinct.sizeIs == ids.count(_.isTeacherId)
  }

  property("External IDs are unique") = forAll(generateUniqueIDs(20)) { ids =>
    ids.filter(_.isExternalId).distinct.sizeIs == ids.count(_.isExternalId)
  }



