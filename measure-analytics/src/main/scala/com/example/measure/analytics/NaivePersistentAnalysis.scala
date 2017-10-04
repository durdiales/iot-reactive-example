package com.example.measure.analytics

import com.example.measure.analytics.cassandra.{CassandraSinkProvider, SparkCassandraConfig}
import com.example.measure.analytics.processing.MeasureEventDecoder
import com.example.measure.analytics.source.KafkaJsonEventSource

/**
  * Just some simple sample of how to persist on Cassandra a flatten version of the measure events.
  *
  * @author jazumaquero
  */
object NaivePersistentAnalysis extends App with SparkCassandraConfig with KafkaJsonEventSource with MeasureEventDecoder {
  override protected val master = "local[*]"
  override protected val appName = "Measure Analytics - Naive Persistent Example"
  override implicit protected val bootstrapServers: String = "localhost:9092"
  override implicit protected val topic: String = "pub_measure"

  override protected def clusterSeeds: String = "127.0.0.1"
  override protected def port: String = "4000"

  val measures = decodeMeasures(events)

  val sink = measures.writeStream
    .option("checkpointLocation", "checkpoint")
    .option("keySpace", "datalogger")
    .option("table", "naive")
    .option("primaryKeys", "id,name")
    .option("clusteringKeys", "t")
    .format("com.example.measure.analytics.cassandra.CassandraSinkProvider")
    .start()

  sink.awaitTermination()
}
