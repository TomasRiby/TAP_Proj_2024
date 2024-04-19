package pj.io

import scala.xml.Elem
import pj.domain.{Availability, Result, VivaResult, DomainError}
import pj.xml.XML

object ScheduleIO:

  def createScheduleError(error: DomainError): Result[Elem] =
    Right(
      <error xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../../scheduleError.xsd"
             message={error.message}>
      </error>
    )

  def createScheduleXML(vivaResult: List [VivaResult], totalPreference: Int): Result[Elem] =
    Right(
      <schedule xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../../schedule.xsd" totalPreference={totalPreference.toString}>
        {vivaResult.map { viva =>
          <viva student="Student Name Placeholder" title="Viva Title Placeholder" start={viva.availabilities.start.toString} end={viva.availabilities.end.toString} preference={viva.availabilities.preference.toString}>
            <president name={viva.president.name.toString}/>
            <advisor name={viva.advisor.name.toString}/>
              {viva.supervisor.map { supervi =>
            <supervisor name={supervi.name.toString}/>
              }
            }
          </viva>
        }}
      </schedule>)


//<viva student="Student Name Placeholder" title="Viva Title Placeholder" start={availability.start.toString} end={availability.end.toString} preference={availability.preference.toString}>
//  <president name="Teacher Placeholder"/>
//  <advisor name="Advisor Placeholder"/>
//  <supervisor name="Supervisor Placeholder"/>
//</viva>