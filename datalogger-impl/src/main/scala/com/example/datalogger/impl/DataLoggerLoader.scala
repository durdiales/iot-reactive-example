package com.example.datalogger.impl

import com.example.datalogger.api.DataLoggerService
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.server.{LagomApplicationContext, LagomApplicationLoader}

/**
  * Loads the implementation of the data logger api defined on {@link DataLoggerService} trait.
  *
  * @author jazumaquero
  */
class DataLoggerLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext) =
    new DataLoggerApplication(context) {
      override def serviceLocator = ServiceLocator.NoServiceLocator
    }

  override def describeService = Some(readDescriptor[DataLoggerService])
}