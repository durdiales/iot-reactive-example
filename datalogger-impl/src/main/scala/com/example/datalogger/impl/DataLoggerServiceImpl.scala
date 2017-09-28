package com.example.datalogger.impl

import com.example.datalogger.api.{AddMeasure, DataLoggerService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext

/**
  * Implementation of the {@link DataLoggerService}.
  *
  * @author jazumaquero
  */
class DataLoggerServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(implicit ctx: ExecutionContext) extends DataLoggerService {
  /** Just a simple logger. **/
  private final val logger: Logger = LoggerFactory.getLogger(classOf[DataLoggerServiceImpl])

  override def addMeasure = ServiceCall { measurement =>
    logger.info(s"Requested following measurement: $measurement")
    persistentEntityRegistry.refFor[MeasureEntity](measurement.id).ask(measurement)
  }

  override def publishMeasure : Topic[AddMeasure] = TopicProducer.singleStreamWithOffset { fromOffset =>
    persistentEntityRegistry.eventStream(MeasureEvent.Instance, fromOffset) map { event =>
      event.event match {
        case AddMeasureEvent(measure) => (measure, event.offset)
      }
    }
  }
}
