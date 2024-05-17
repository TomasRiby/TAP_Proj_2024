package pj.io

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import pj.domain.{Availability, Result}
import pj.opaqueTypes.{OTime, Preference}

import scala.xml.Elem

class ResourceIOTest extends AnyFunSuite with Matchers:
  val simpleXml = "files/test/ms01/simple01.xml"

  test("Load values from xml and instance proper Viva"):
    val resultElem: Result[Elem] = FileIO.load(simpleXml);
    val result = resultElem.fold(
      error => Left(error),
      elem => ResourceIO.loadResources(elem)
    )

    val startDate = OTime.createTime("2024-05-30T09:30:00")
    val endDate = OTime.createTime("2024-05-30T12:30:00")
    val preference = Preference.createPreference(2)

    (result, startDate, endDate) match
      case (Right(resource), Right(sd), Right(ed)) =>
        resource.external.foreach(ext => {
          ext.availability.foreach(av => {
            startDate match
              case Right(st) => assert(st === sd)
              case _ => fail("start date is wrong")

            endDate match
              case Right(et) => assert(et === ed)
              case _ => fail("end date is wrong")
          })
        })
      case (_) => fail("Did not create VIVA")


