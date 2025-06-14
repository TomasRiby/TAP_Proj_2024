package pj.io

import scala.xml.Elem
import pj.domain.{Availability, DomainError, Result, ScheduleOut}
import pj.xml.XML

import java.time.format.DateTimeFormatter

object ScheduleIO:

  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  def createScheduleError(error: DomainError): Result[Elem] =
    Right(
      <error xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../../scheduleError.xsd"
             message={error.message}>
      </error>
    )

  def createScheduleXML(scheduleOut: ScheduleOut): Result[Elem] =
    Right(
      <schedule xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../../schedule.xsd"
                totalPreference={scheduleOut.preference.toString}>
        {scheduleOut.posVivas.map { viva =>
        <viva student={viva.student} title={viva.title} start={viva.start} end={viva.end} preference={viva.preference.toString}>
          <president name={viva.president}/>
          <advisor name={viva.advisor}/>{viva.coAdvisors.map(coAdvisor => <coadvisor name={coAdvisor}/>)}{viva.supervisors.map(supervisor => <supervisor name={supervisor}/>)}
        </viva>
      }}
      </schedule>
    )