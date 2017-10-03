package com.example.datalogger.impl

import com.example.datalogger.api.DataLoggerService
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext}
import com.softwaremill.macwire._
import play.api.libs.ws.ahc.AhcWSComponents

/**
  * Abstract application class used to configure and load the sever is going to run the class
  *
  * @param context
  * @author jazumaquero
  */
abstract class DataLoggerApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents
    with CassandraPersistenceComponents {

  override lazy val lagomServer = serverFor[DataLoggerService](wire[DataLoggerServiceImpl])
  override lazy val jsonSerializerRegistry = MeasureSerializerRegistry

  persistentEntityRegistry.register(wire[MeasureEntity])
}
