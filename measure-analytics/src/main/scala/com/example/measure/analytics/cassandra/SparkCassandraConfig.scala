package com.example.measure.analytics.cassandra

import com.example.measure.analytics.common.SparkConfig
import org.apache.spark.SparkConf


/**
  * Use instead of {@link SparkConfig} to allow connecting with Cassandra.
  *
  * @author jazumaquero
  */
trait SparkCassandraConfig extends SparkConfig {
  /** Current version is some host from the cluster **/
  protected def clusterSeeds: String

  /** Current version is the port where cassandra is listening **/
  protected def port: String

  override protected val sparkConf = Some(
    new SparkConf()
      .set("spark.cassandra.connection.host", clusterSeeds)
      .set("spark.cassandra.connection.port", port)
  )
}
