package com.example.datareader.impl

import com.example.datareader.api.ReadMeasureCommand
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer

/**
  * This class defines any state that can take any {@link ReadMeasureCommand} implementation.
  *
  * @param command
  * @param logged
  * @author durdiales
  */
final case class ReadMeasureState(command: Option[ReadMeasureCommand], logged: Boolean)

/**
  * Includes all static members from {@link ReadMeasureState}
  *
  * @author durdiales
  */
object ReadMeasureState {
  /** Initial state. **/
  val empty: ReadMeasureState = ReadMeasureState(None, false)

  import play.api.libs.json._

  /** Allows reading/writing {@link ReadMeasureCommand} from/to json string. **/
  implicit val readMeasureCommandFormat: Format[ReadMeasureCommand] = Json.format[ReadMeasureCommand]

  /** Allows reading/writing {@link MeasureState} from/to json string. **/
  implicit val readMeasureStateFormat: Format[ReadMeasureState] = Json.format[ReadMeasureState]

  /** Convenient access to all json serializers. **/
  val serializers = Vector(JsonSerializer(readMeasureStateFormat))
}
