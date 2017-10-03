package com.example.measure.analytics.processing

import com.example.measure.analytics.common.{SparkFixture, SparkMatcher}
import org.apache.spark.sql.{DataFrame, Row}
import org.scalatest.{FlatSpec, Matchers}

/**
  * This spec verifies that {@link MeasureEventDecoder} works as expected to
  *
  * @author jazumaquero
  */
class MeasureEventDecoderSpec extends FlatSpec with Matchers with SparkMatcher with MeasureEventDecoder {
  /** Use spark session from fixture. **/
  protected val spark = SparkFixture.spark
  import spark.implicits._

  behavior of "MeasureEventDecoder"
  it should "decode well formatter message (smoke test)" in {
    val path: String = getClass.getResource("/json").getPath
    val events : DataFrame = spark.sparkContext.textFile(path).toDF
    val current : DataFrame = decodeMeasures(events)
    // Initially only 3 measures must be included
    current.count should equal(3)
    // No measure from 'foo' are expected
    current.where($"id" === "foo").count should equal(0)
    // One only measure from 'bar' are expected
    current.where($"id" === "bar").count should equal(1)
    current.where($"id" === "bar" && $"name" === "v1" && $"value" === 1.23).count should equal(1)
    // Two measures from 'tar' are expected
    current.where($"id" === "tar").count should equal(2)
    current.where($"id" === "tar" && $"name" === "v1" && $"value" === 4.56).count should equal(1)
    current.where($"id" === "tar" && $"name" === "v2" && $"value" === 78.9).count should equal(1)
  }
}
