package com.example.measure.analytics.cassandra

import com.datastax.spark.connector.cql.CassandraConnector
import com.datastax.spark.connector.embedded.{EmbeddedCassandra, SparkTemplate, YamlTransformations}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Verifies that Cassandra sink works fine.
  *
  * @author
  */
class CassandraSinkProviderSpec extends FlatSpec with Matchers with EmbeddedCassandra with SparkTemplate {
  // Ensure everything is properly configured
  //useCassandraConfig(Seq(YamlTransformations.Default),true)
  useSparkConf(defaultConf)

  /** Connector that allows to execute queries on cassandra **/
  protected def connector = CassandraConnector(defaultConf)

  /** Convenient class for making test more easier. **/
  sealed case class Foo(pk: String, ck: String, value: Double)

  protected val dataset = Seq(Foo("foo", "bar", 1), Foo("tar", "nav", 2.3))
  val keySpace: String = "test"
  val table: String = "kv"
  val primaryKeys: String = "pk"
  val clusteringKeys: String = "ck"

  behavior of "CassandraSinkProvider"
  it should "" in {
    val spark = sparkSession
    import spark.implicits._
    connector.withSessionDo { session =>
      session.execute(s"CREATE KEYSPACE IF NOT EXIST $keySpace")
    }
    dataset.toDF
      .writeStream
      .option("checkpointLocation", "checkpoint")
      .option("keySpace", keySpace)
      .option("table", table)
      .option("primaryKeys", primaryKeys)
      .option("clusteringKeys", clusteringKeys)
      .format("com.example.measure.analytics.cassandra.TestCassandraSinkProvider")
      .start()
      .awaitTermination(10000)
    connector.withSessionDo { session =>
      val result = session.execute(s"SELECT * FROM $keySpace.$table")
      result
    }
  }

  override def clearCache(): Unit = {}
}
