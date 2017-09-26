package com.example.datalogger.impl

import akka.Done
import akka.actor.ActorSystem
import com.example.datalogger.api.{AddMeasure, Measure, MeasureCommand}
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class MeasureEntitySpec extends WordSpecLike with Matchers with BeforeAndAfterAll {

  lazy val measurementId: String = "measure-id"
  lazy val addMeasureCommand: AddMeasure = AddMeasure("1234-fgh", DateTime.now, List(Measure("foo", 0.0123)))
  val system = ActorSystem(getClass.getSimpleName, JsonSerializerRegistry.actorSystemSetupFor(MeasureSerializerRegistry))

  override def afterAll(): Unit = {
    Await.ready(system.terminate, 10.seconds)
  }

  "MeasureEntity" must {
    "handle AddMeasure" in {
      val driver = new PersistentEntityTestDriver(system, new MeasureEntity, measurementId)
      val content: MeasureCommand = addMeasureCommand
      val outcome = driver.run(addMeasureCommand)
      outcome.events should ===(List(AddMeasureEvent(addMeasureCommand)))
      outcome.state.logged should ===(true)
      outcome.state.command should ===(Some(addMeasureCommand))
      outcome.replies should ===(List(Done))
      outcome.issues should be(Nil)
    }
  }
}
