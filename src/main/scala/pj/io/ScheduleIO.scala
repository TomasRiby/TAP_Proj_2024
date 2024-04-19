package pj.io

import scala.xml.Elem
import pj.domain.{Availability, DomainError, Result}
import pj.xml.XML

object ScheduleIO:

  def createScheduleError(error: DomainError): Result[Elem] =
    Right(
      <error xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../../scheduleError.xsd"
             message={error.message}>
      </error>
    )

  def createScheduleXML(availabilities: List[Availability], totalPreference: Int): Result[Elem] = {
    Right(<schedule xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../../schedule.xsd" totalPreference={totalPreference.toString}>
      {availabilities.map { availability =>
      <viva student="Student Name Placeholder" title="Viva Title Placeholder" start={availability.start.toString} end={availability.end.toString} preference={availability.preference.toString}>
        <president name="Teacher Placeholder"/>
        <advisor name="Advisor Placeholder"/>
        <supervisor name="Supervisor Placeholder"/>
      </viva>
    }}
    </schedule>)
  }