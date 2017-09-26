package com.example.datalogger.impl

import com.example.datalogger.api.MeasureCommand
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer

/**
  * This class defines any state that can take any {@link MeasureCommand} implementation.
  *
  * @param command
  * @param logged
  * @author jazumaquero
  */
final case class MeasureState(command: Option[MeasureCommand], logged: Boolean)

/**
  * Includes all static members from {@link MeasureState}
  *
  * @author jazumaquero
  */
object MeasureState {
  /** Initial state. **/
  val empty: MeasureState = MeasureState(None, false)

  import play.api.libs.json._

  /** Allows reading/writing {@link MeasureCommand} from/to json string. **/
  implicit val measureCommandFormat: Format[MeasureCommand] = Json.format[MeasureCommand]

  /** Allows reading/writing {@link MeasureState} from/to json string. **/
  implicit val measureStateFormat: Format[MeasureState] = Json.format[MeasureState]

  /** Convenient access to all json serializers. **/
  val serializers = Vector(JsonSerializer(measureStateFormat))
}
