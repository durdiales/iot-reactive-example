package com.example.measure.analytics.source

import com.example.measure.analytics.common.SparkComponent
import org.apache.spark.sql.DataFrame

/**
  * Allows to consume events from some specific Kafka topic, which are serialized with JSON.
  *
  * @author jazumaquero
  */
trait KafkaJsonEventSource extends SparkComponent {
  /** Comma separated list of host:port from the kafka bootstrap servers. **/
  implicit protected val bootstrapServers: String

  /** Name of the topic where events are expected to be. **/
  implicit protected val topic: String

  /**
    * Get all json events from the kafka topic.
    *
    * @return a DataFrame with one only column 'value' that includes all messages coded like strings.
    */
  def events: DataFrame = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", bootstrapServers)
    .option("subscribe", topic)
    .load()
    .selectExpr("CAST(value AS STRING)")
}
