package com.example.measure.analytics.common

import org.apache.spark.SparkConf
import org.apache.spark.sql.Dataset
import org.scalatest.Matchers

trait SparkBehavior extends SparkConfig {
  override protected val master: String = "local[*]"
  override protected val appName: String = s"${getClass.getName}"
  override protected val sparkConf: Option[SparkConf] = None
}

trait SparkMatcher extends Matchers {
  def assertDataSetsAreEqual[T](expected: Dataset[T], current: Dataset[T]): Unit = {
    current.schema should equal(expected.schema)
    current.collect.exists(expected.collect.contains) should be(true)
  }
}

object SparkFixture extends SparkBehavior
