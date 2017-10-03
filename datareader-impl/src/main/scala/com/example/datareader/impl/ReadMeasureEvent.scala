package com.example.datareader.impl

import com.example.datareader.api.ReadMeasure
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer

/**
  * This trait should be extend by any event related to some {@link ReadMeasureCommand}
  *
  * @author durdiales
  */
sealed trait ReadMeasureEvent extends AggregateEvent[ReadMeasureEvent] {
  override def aggregateTag: AggregateEventShards[ReadMeasureEvent] = ReadMeasureEvent.Tag
}

/**
  * This trait defines the event related to the {@link AddMeasure} command.
  *
  * @param measure
  * @author durdiales
  */
final case class GetMeasureEvent(measure: ReadMeasure) extends ReadMeasureEvent

/**
  * Includes all static members from {@link ReadMeasureEvent} trait
  *
  * @author durdiales
  */
object ReadMeasureEvent {
  /** TODO **/
  val NumShards = 10
  /** TODO **/
  val Tag = AggregateEventTag.sharded[ReadMeasureEvent](NumShards)
  /** TODO **/
  val Instance: AggregateEventTag[ReadMeasureEvent] = AggregateEventTag[ReadMeasureEvent]()

  import play.api.libs.json._

  /** Allows reading/writing {@link GetMeasureEvent} command from/to json string. **/
  implicit val getMeasureEventFormat: Format[GetMeasureEvent] = Json.format[GetMeasureEvent]

  /** Convenient access to all json serializers. **/
  val serializers = Vector(JsonSerializer(getMeasureEventFormat))
}
