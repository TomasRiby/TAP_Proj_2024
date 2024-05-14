package pj.xml

import pj.domain.*

import java.time.format.DateTimeFormatter
import scala.xml.Elem

object DomainToXML {

  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  private def supervisorToXml(supervisor: External | Teacher): Elem = supervisor match
    case teacher: Teacher =>
        <supervisor name={teacher.name.toString}/>
    case external: External =>
        <supervisor name={external.name.toString}/>

  private def coadvisorToXml(coadvisor: External | Teacher): Elem = coadvisor match
    case teacher: Teacher =>
        <coadvisor name={teacher.name.toString}/>
    case external: External =>
        <coadvisor name={external.name.toString}/>

  private def vivaToXml(scheduledViva: VivaScheduled): Elem =
    <viva student={scheduledViva.student.toString} title={scheduledViva.title.toString} start={scheduledViva.start.toLocalDateT.format(formatter)} end={scheduledViva.end.toLocalDateT.format(formatter)} preference={scheduledViva.preference.toString}>
      <president name={scheduledViva.president.name.toString}/>
      <advisor name={scheduledViva.advisor.name.toString}/>{scheduledViva.coadvisors.map(coadvisorToXml)}{scheduledViva.supervisors.map(supervisorToXml)}
    </viva>

  def scheduleToXml(agenda: ScheduleAgenda): Elem =
    <schedule xsi:noNamespaceSchemaLocation="../../schedule.xsd" totalPreference={agenda.preference.toString} xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      {agenda.vivas.map(vivaToXml)}
    </schedule>

  def errorToXml(error: DomainError): Elem =
      <error xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../../scheduleError.xsd"
             message={error.toString}/>
}