package com.example.datalogger.impl

import com.example.datalogger.api.AddMeasure
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer

/**
  * This trait should be extend by any event related to some {@link MeasureCommand}
  *
  * @author jazumaquero
  */
sealed trait MeasureEvent extends AggregateEvent[MeasureEvent] {
  override def aggregateTag: AggregateEventShards[MeasureEvent] = MeasureEvent.Tag
}

/**
  * This trait defines the event related to the {@link AddMeasure} command.
  *
  * @param measure
  * @author jazumaquero
  */
final case class AddMeasureEvent(measure: AddMeasure) extends MeasureEvent

/**
  * Includes all static members from {@link MeasureEvent} trait
  *
  * @author jazumaquero
  */
object MeasureEvent {
  /** TODO **/
  val NumShards = 10
  /** TODO **/
  val Tag = AggregateEventTag.sharded[MeasureEvent](NumShards)
  /** TODO **/
  val Instance: AggregateEventTag[MeasureEvent] = AggregateEventTag[MeasureEvent]()

  import play.api.libs.json._

  /** Allows reading/writing {@link AddMeasureEvent} command from/to json string. **/
  implicit val addMeasureEventFormat: Format[AddMeasureEvent] = Json.format[AddMeasureEvent]

  /** Convenient access to all json serializers. **/
  val serializers = Vector(JsonSerializer(addMeasureEventFormat))
}
