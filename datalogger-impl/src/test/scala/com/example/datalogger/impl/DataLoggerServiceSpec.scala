package com.example.datalogger.impl

import akka.Done
import akka.stream.testkit.scaladsl.TestSink
import com.example.datalogger.api.{AddMeasure, DataLoggerService, GetMeasures, Measure}
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.{ServiceTest, TestTopicComponents}
import org.joda.time.DateTime
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

import scala.concurrent.duration.FiniteDuration

/**
  * Test to validate if {@link DataLoggerService} works fine.
  *
  * @author jazumaquero
  */
class DataLoggerServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  lazy val measurement: AddMeasure = AddMeasure("1234-fgh", DateTime.now, List(Measure("foo", 0.0123)))

  lazy val server = ServiceTest.startServer(ServiceTest.defaultSetup.withCassandra(true)) { ctx =>
    new DataLoggerApplication(ctx) with LocalServiceLocator with TestTopicComponents {

    }
  }
  lazy val client = server.serviceClient.implement[DataLoggerService]
  implicit lazy val system = server.actorSystem
  implicit lazy val mat = server.materializer

  override protected def beforeAll() = server

  override protected def afterAll() = server.stop()

  "DataLoggerService" should {
    "properly take new measures" in {
      client.addMeasure.invoke(measurement).map { response =>
        response should equal(Done)
      }
    }
    "publish new measure on topic " in {
      client.addMeasure.invoke(measurement).map { _ =>
        // Produce will sent message to the topic some time later that event were persisted,
        // so that, it's required to include some delay (workaround to forget about configuration
        // is putting some 'large' delay.
        val source = client.publishMeasure().subscribe.atMostOnceSource
        val result = source.runWith(TestSink.probe[AddMeasure]).requestNext(FiniteDuration(20, "seconds"))
        result should equal(measurement)
      }
    }
    "request for latest measure" in {
      client.addMeasure.invoke(measurement).map { _ =>
        client.getLatestMeasure(measurement.id).invoke map { result =>
          result should equal(measurement)
        }
      }
      assert(true)
    }
    "request for measured datain a time range" in {
      client.addMeasure.invoke(measurement).map { _ =>
        val id = measurement.id
        val name = measurement.metrics(0).name
        val init = measurement.metrics(0).t
        val end = init
        client.getMeasures(id).invoke(GetMeasures(name, init, end)).map { response =>
          response should equal(measurement)
        }
      }
      assert(true)
    }
  }
}
