// Project settings
organization in ThisBuild := "com.example"
version in ThisBuild := "0.1.0-SNAPSHOT"

// Build Settings
scalaVersion in ThisBuild := "2.11.8"

// Runtime properties
lagomServiceLocatorPort in ThisBuild := 10000

// Dependencies
val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test
val sparkCore = "org.apache.spark" %% "spark-core" % "2.2.0"
val sparkSql = "org.apache.spark" %% "spark-sql" % "2.2.0"
val sparkStreaming = "org.apache.spark" %% "spark-streaming" % "2.2.0"
val sparkSqlKafka = "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.2.0"
val sparkCassandra = "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.5"
val sparkCassandraEmbedded = "com.datastax.spark" %% "spark-cassandra-connector-embedded" % "2.0.5" % Test

// Define global build in this multiproject
lazy val `iot-reactive` = (project in file(".")).aggregate(`datalogger-api`, `datalogger-impl`, `measure-analytics`)

// Define datalogger-api project build
lazy val `datalogger-api` = (project in file("datalogger-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi, lagomScaladslPersistence
    )
  )

// Define datalogger-impl project build
lazy val `datalogger-impl` = (project in file("datalogger-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslPubSub,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(
    lagomForkedTestSettings: _*
  )
  .dependsOn(`datalogger-api`)

// Define measure-analytics project build
lazy val `measure-analytics` = (project in file("measure-analytics"))
  .settings(
    libraryDependencies ++= Seq(
      sparkCore,
      sparkSql,
      sparkStreaming,
      sparkSqlKafka,
      sparkCassandra,
      scalaTest,
      sparkCassandraEmbedded
    )
  )
