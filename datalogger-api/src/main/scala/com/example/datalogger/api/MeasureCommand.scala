package com.example.datalogger.api

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import org.joda.time.DateTime
import org.joda.time.DateTime._


/**
  * Includes any information related to some metric measurement.
  *
  * @param name  is the name of the metric has been measured.
  * @param value is the value of the metric has been measured.
  * @param t     is when the value of the metrics was measured.
  * @author jazumaquero
  */
final case class Measure(name: String, value: Double, t: DateTime = now())

/**
  * Common trait that defines all possible commands related to measurement domain.
  *
  * @author jazumaquero
  */
sealed trait MeasureCommand

/**
  * Defines some command to push information about a collection of metrics from some some device.
  *
  * @param id      from device who is sending metrics.
  * @param tstamp  when metrics where sent.
  * @param metrics is a list of measures from device
  * @author jazumaquero
  */
final case class AddMeasure(id: String, tstamp: DateTime, metrics: List[Measure]) extends MeasureCommand with ReplyType[Done]

/**
  * Include all required formats to deal with {@link MeasureCommands}
  *
  * @author jazumaquero
  * @todo Load
  */
object MeasureCommand {

  import play.api.libs.json._

  /** Literal that includes the way timestamp is going to be formatted **/
  protected val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
  /** Allows reading/writing {@link DateRime} from/to json string. **/
  implicit val dateFormat = Format[DateTime](Reads.jodaDateReads(pattern), Writes.jodaDateWrites(pattern))
  /** Allows reading/writing {@link Measure} from/to json string **/
  implicit val measureFormat: Format[Measure] = Json.format[Measure]
  /** Allows reading/writing {@link AddMeasure} command from/to json string. **/
  implicit val addMeasureformat: Format[AddMeasure] = Json.format[AddMeasure]

  /** Convenient access to all json serializers. **/
  val serializers = Vector(JsonSerializer(addMeasureformat))

  /**
    * This applys allows to deal with serialization from  {@link MeasureCommand} sealed trait
    *
    * @param command to be serialized
    * @return
    */
  def unapply(command: MeasureCommand): Option[(String, JsValue)] = {
    val (prod: Product, sub) = command match {
      case addMeasure: AddMeasure => (addMeasure, Json.toJson(addMeasure)(addMeasureformat))
    }
    Some(prod.productPrefix -> sub)
  }

  /**
    * This applys allows to deal with de-serialization from  {@link MeasureCommand} sealed trait
    *
    * @param `class` reference is going to be deserialized
    * @param data    in a json serialized form
    * @return
    */
  def apply(`class`: String, data: JsValue): MeasureCommand = {
    (`class` match {
      case "AddMeasure" => Json.fromJson[AddMeasure](data)(addMeasureformat)
    }).get
  }
}