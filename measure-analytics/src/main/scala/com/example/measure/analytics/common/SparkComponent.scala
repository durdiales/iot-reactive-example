package com.example.measure.analytics.common

import org.apache.spark.sql.SparkSession

/**
  * It's recommended to extend/mix this trait that give access to some singleton spark session,
  * just for making easy doing dependency injection using the piece of cake pattern
  *
  * @author jazumaquero
  */
trait SparkComponent {

  /** Some spark session shared by all extended traits. **/
  implicit protected val spark: SparkSession
}
