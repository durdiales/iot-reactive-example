package com.example.measure.analytics.common

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Configuration trait that allows to build some spark session in some easy way.
  *
  * It may be useful when you are developing in local, but not too much for production environments.
  *
  * @author jazumaquero
  */
trait SparkConfig extends SparkComponent {
  /** Set with the name of the master. **/
  protected val master: String

  /** Set to the name of the application. **/
  protected val appName: String

  /** Set to None if you want default config, or override it with your custom configuration. **/
  protected val sparkConf: Option[SparkConf]

  /** Spark session. **/
  lazy val spark: SparkSession = sparkConf match {
    case Some(conf) => SparkSession.builder().appName(appName).master(master).config(conf).getOrCreate()
    case _ => SparkSession.builder().appName(appName).master(master).getOrCreate()
  }
}