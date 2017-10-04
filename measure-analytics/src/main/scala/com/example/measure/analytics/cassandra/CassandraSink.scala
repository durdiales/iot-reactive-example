package com.example.measure.analytics.cassandra

import org.apache.spark.sql.execution.streaming.Sink
import org.apache.spark.sql.{DataFrame, SaveMode}

import scala.util.Try

/**
  * Sink that allows writing data from some stream directly to cassandra, no matter
  * the schema is used.
  *
  * Remember taking a look how Cassandra deals with primary and clustering keys.
  *
  * @param keySpace       where data is going to be stored
  * @param table          where data is going to be stored
  * @param primaryKeys    put here all columns from dataframe that are going to be used as primary keys
  * @param clusteringKeys put here all columns from dataframe that are going to be used as clustering keys
  * @author jazumaquero
  */
class CassandraSink(keySpace: String,
                    table: String,
                    primaryKeys: Option[Seq[String]],
                    clusteringKeys: Option[Seq[String]]
                   ) extends Sink {

  override def addBatch(batchId: Long, data: DataFrame): Unit = {
    import com.datastax.spark.connector._
    import org.apache.spark.sql.cassandra._
    // Ensure table is created
    Try(data.createCassandraTable(keySpace, table, primaryKeys, clusteringKeys)) match {
      // Avoided throwing exceptions in case of table was previously created
      case _ => Unit
    }
    // Append new data
    data.write.cassandraFormat(table, keySpace).mode(SaveMode.Append).save()
  }
}
