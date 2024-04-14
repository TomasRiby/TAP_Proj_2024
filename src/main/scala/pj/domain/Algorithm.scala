package pj.domain

import pj.domain.Algorithm.intersectAvailability

object Algorithm:
  

  private def intersectAvailability(resources: List[Any]): Result[List[Availability]] = ???
    
  
  /*
   def intersectAvailability(a1: Availability, a2: Availability): Option[Availability] = {
     val startMax = if (a1.start.isAfter(a2.start)) a1.start else a2.start
     val endMin = if (a1.end.isBefore(a2.end)) a1.end else a2.end
     if (startMax.isBefore(endMin)) Some(Availability(startMax, endMin, a1.preference + a2.preference))
     else None
   }

def intersectAvailabilities(lists: List[Availability]*): List[Availability] = {
  lists.toList.flatten.combinations(3).toList.flatMap {
    case a1 :: a2 :: a3 :: Nil =>
      for {
        firstIntersection <- intersectAvailability(a1, a2)
        secondIntersection <- intersectAvailability(firstIntersection, a3)
      } yield secondIntersection
    case _ => List.empty
  }
}
  */

/*
  def findVivaIntersections(vivas: List[Viva], teachers: List[Teacher], externals: List[External]): List[Viva] = {
    vivas.flatMap { viva =>
      val presidentAvail = teachers.find(_.id == viva.president.id).toList.flatMap(_.availabilities)
      val advisorAvail = teachers.find(_.id == viva.advisor.id).toList.flatMap(_.availabilities)
      val supervisorAvail = externals.find(_.id == viva.supervisor.id).toList.flatMap(_.availabilities)
  
      val commonAvailabilities = intersectAvailabilities(presidentAvail, advisorAvail, supervisorAvail)
      commonAvailabilities.map(avail => Viva(viva.studentId, viva.title, viva.president, viva.advisor, viva.supervisor, avail))
    }
  }
*/