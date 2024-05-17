package pj.domain


import java.time.{Duration, LocalDateTime}


object AlgorithmV2:
  def makeTheAlgorithmHappen(agenda: Agenda): Result[Unit] =
    val teacherList = agenda.resources.teacher
    val externalList = agenda.resources.external

    val preVivaList = agenda.vivas.map(PreViva.linkVivaWithResource(_, teacherList, externalList))

    def getEarliestPresidentAvailability(preViva: PreViva): Option[LocalDateTime] = {
      preViva.roleLinkedWithResourceList
        .filter(_.role.isInstanceOf[President]) // Filter for President role
        .flatMap {
          case RoleLinkedWithResource(_, resource: Teacher) => resource.availability.map(_.start)
          case RoleLinkedWithResource(_, resource: External) => resource.availability.map(_.start)
        }
        .sorted // Sort availabilities by start time
        .headOption // Get the start time of the earliest availability
    }

    // Sorting the list of PreViva by the earliest availability of the President role
    val sortedPreVivaList = preVivaList.sortBy(getEarliestPresidentAvailability)

    println(sortedPreVivaList)

    Right(())


