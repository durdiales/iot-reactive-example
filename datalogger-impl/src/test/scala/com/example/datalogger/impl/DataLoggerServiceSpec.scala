package com.example.datalogger.impl

import akka.NotUsed
import com.example.datalogger.api.{AddMeasure, DataLoggerService, Measure}
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.joda.time.DateTime
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

/**
  * Test to validate if {@link DataLoggerService} works fine.
  *
  * @author jazumaquero
  */
class DataLoggerServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  lazy val measurement: AddMeasure = AddMeasure("1234-fgh", DateTime.now, List(Measure("foo", 0.0123)))

  lazy val server = ServiceTest.startServer(ServiceTest.defaultSetup) { ctx =>
    new DataLoggerApplication(ctx) with LocalServiceLocator
  }
  lazy val client = server.serviceClient.implement[DataLoggerService]

  override protected def beforeAll() = server

  override protected def afterAll() = server.stop()

  "DataLoggerService" should {
    "properly take new measures" in {
      client.addMeasure.invoke(measurement).map { response =>
        response should equal(NotUsed)
      }
    }
  }
}
