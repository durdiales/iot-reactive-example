package com.example.measure.analytics

import com.example.measure.analytics.common.SparkConfig
import com.example.measure.analytics.processing.MeasureEventDecoder
import com.example.measure.analytics.source.KafkaJsonEventSource

/**
  * Just some simple sample of how to print on console a flatten version of the measure events.
  *
  * @author jazumaquero
  */
object NaiveAnalysis extends App with SparkConfig with KafkaJsonEventSource with MeasureEventDecoder {
  override protected val master = "local[*]"
  override protected val appName = "Measure Analytics - Naive Example"
  override protected val sparkConf = None
  override implicit protected val bootstrapServers: String = "localhost:9092"
  override implicit protected val topic: String = "pub_measure"

  val sink = decodeMeasures(events)
    .writeStream
    .format("console")
    .start()

  sink.awaitTermination()
}
