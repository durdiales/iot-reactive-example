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

// Define global build in this multiproject
lazy val `iot-reactive` = (project in file(".")).aggregate(`datalogger-api`, `datalogger-impl`)

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
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(
    lagomForkedTestSettings: _*
  )
  .dependsOn(`datalogger-api`)
