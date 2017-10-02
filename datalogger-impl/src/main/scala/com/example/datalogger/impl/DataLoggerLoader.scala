package com.example.datalogger.impl

import com.example.datalogger.api.DataLoggerService
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader}

/**
  * Loads the implementation of the data logger api defined on {@link DataLoggerService} trait.
  *
  * @author jazumaquero
  */
class DataLoggerLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext) =
    new DataLoggerApplication(context) with LagomKafkaComponents {
      override def serviceLocator = ServiceLocator.NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new DataLoggerApplication(context) with LagomDevModeComponents  with LagomKafkaComponents

  override def describeService = Some(readDescriptor[DataLoggerService])
}