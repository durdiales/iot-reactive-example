package com.example.datalogger.impl

import akka.NotUsed
import com.example.datalogger.api.DataLoggerService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future

/**
  * Implementation of the {@link DataLoggerService}.
  *
  * @author jazumaquero
  */
class DataLoggerServiceImpl extends DataLoggerService {
  /** Just a simple logger. **/
  private final val logger : Logger = LoggerFactory.getLogger(classOf[DataLoggerServiceImpl])

  override def addMeasure = ServiceCall { measurement =>
    logger.info(s"Requested following measurement: $measurement")
    Future.successful(NotUsed)
  }
}
