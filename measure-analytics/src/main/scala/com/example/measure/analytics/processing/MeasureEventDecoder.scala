package com.example.measure.analytics.processing

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, explode, from_json, unix_timestamp}
import org.apache.spark.sql.types._

/**
  * Give some transformation method that allows to extract measures from some {@link DataFrame}
  * that have a column with those measured encoded in JSON with the known format.
  *
  * @author jazumaquero
  */
trait MeasureEventDecoder {

  /** Timestamp format **/
  protected lazy val timestampFormat = "yyyy-MM-dd'T'HH:mm:ss"

  /** Schema for agnostic measure. **/
  protected lazy val measureSchema: StructType = StructType(Seq())
    .add("name", StringType)
    .add("value", DoubleType)
    .add("t", StringType)

  /** Schema for the whole measure. **/
  protected lazy val measureEventSchema: StructType = StructType(Seq())
    .add("id", StringType)
    .add("tstamp", StringType)
    .add("metrics", ArrayType(measureSchema))

  /**
    * Decode measure events from some give {@link DataFrame}
    *
    * @param events      is the dataFrame is going to be processed.
    * @param eventColumn is the column name where is expected to find the json within the measures
    * @return
    */
  def decodeMeasures(events: DataFrame, eventColumn: String = "value"): DataFrame = events
    // First: decode JSON with given schema.
    .select(
    from_json(col(eventColumn).cast("string"), measureEventSchema).alias("measures")
  )
    // Second: do some explode in order to flat map the measures
    .select(
    col("measures.id").alias("id"),
    col("measures.tstamp").alias("tstamp"),
    explode(col("measures.metrics")).alias("metrics")
  )
    // Finally: cast timestamp values to TimestampType
    .select(
    col("id"),
    unix_timestamp(col("tstamp"), timestampFormat).alias("tstamp"),
    col("metrics.name").alias("name"),
    col("metrics.value").alias("value"),
    unix_timestamp(col("metrics.t"), timestampFormat).alias("t")
  )
}
