package com.example.datalogger.impl

import com.example.datalogger.api.MeasureCommand
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable

object MeasureSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: immutable.Seq[JsonSerializer[_]] =
    MeasureCommand.serializers ++ MeasureEvent.serializers :+ JsonSerializer[MeasureState]
}
