package com.example.measure.analytics.cassandra

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.sources.StreamSinkProvider
import org.apache.spark.sql.streaming.OutputMode

import scala.util.{Failure, Success, Try}

/**
  * Allows to provide a sink that allows writing data from some stream directly
  * to cassandra, no matter the schema is used.
  *
  * Remember taking a look how Cassandra deals with primary and clustering keys.
  *
  * Options can be taken by the provider to build the sink
  * - keySpace       where data is going to be stored
  * - table          where data is going to be stored
  * - primaryKeys    put here all columns from dataframe that are going to be used as primary keys
  * - clusteringKeys put here all columns from dataframe that are going to be used as clustering keys
  *
  * @author jazumaquero
  */
class CassandraSinkProvider extends StreamSinkProvider {

  override def createSink(sqlContext: SQLContext,
                          parameters: Map[String, String],
                          partitionColumns: Seq[String],
                          outputMode: OutputMode): CassandraSink = {
    val keySpace: String = parameters("keySpace")
    val table: String = parameters("table")
    val primaryKeys: Option[Seq[String]] = Try(parameters("primaryKeys").split(",").map(_.trim)) match {
      case Success(pks) => Some(pks)
      case Failure(_) => None
    }
    val clusteringKeys: Option[Seq[String]] = Try(parameters("clusteringKeys").split(",").map(_.trim)) match {
      case Success(cks) => Some(cks)
      case Failure(_) => None
    }
    new CassandraSink(keySpace, table, primaryKeys, clusteringKeys)
  }
}