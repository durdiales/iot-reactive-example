package com.example.datalogger.impl

import com.example.datalogger.api.{AddMeasure, DataLoggerService, GetLastMeasure}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import org.joda.time.DateTime
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext

/**
  * Implementation of the {@link DataLoggerService}.
  *
  * @author jazumaquero
  */
class DataLoggerServiceImpl(persistentEntityRegistry: PersistentEntityRegistry,
                            cassandraReadSide: CassandraReadSide,
                            readSide: ReadSide,
                            session: CassandraSession)
                           (implicit ctx: ExecutionContext) extends DataLoggerService {
  /** Just a simple logger. **/
  private final val logger: Logger = LoggerFactory.getLogger(classOf[DataLoggerServiceImpl])

  persistentEntityRegistry.register(new MeasureEntity)
  readSide.register(new MeasureEventProcessor(session, cassandraReadSide))

  override def addMeasure = ServiceCall { measurement =>
    logger.info(s"Requested following measurement: $measurement")
    persistentEntityRegistry.refFor[MeasureEntity](measurement.id).ask(measurement) map { reply =>
      reply
    }
  }

  override def getMeasures(id: String) = ServiceCall { request =>
    import MeasureEventProcessor._
    val query = selectMeasuresFromTimeRangeStatement(id, request.name, request.init, request.end)
    logger.info(s"Usign following query: $query")
    session.selectAll(query) map { rows =>
      rows.map(getAddMeasure(_))
        .fold(AddMeasure("", new DateTime(0), List())) { (x, y) =>
          val id = x.id
          val tstamp = if (x.tstamp.isAfter(y.tstamp)) x.tstamp else y.tstamp
          val metrics = x.metrics ++ y.metrics
          AddMeasure(id, tstamp, metrics)
        }
    }
  }

  override def getLatestMeasure(id: String) = ServiceCall { _ =>
    logger.info(s"Requested measures for device id: $id")
    persistentEntityRegistry.refFor[MeasureEntity](id).ask(GetLastMeasure) map { reply =>
      reply
    }
  }

  override def publishMeasure: Topic[AddMeasure] = TopicProducer
    .taggedStreamWithOffset(MeasureEvent.Tag.allTags.toList) { (tag, fromOffset) =>
      persistentEntityRegistry.eventStream(tag, fromOffset) map { event =>
        logger.info(s"Handling event on topic: $event")
        event.event match {
          case AddMeasureEvent(measure) => (measure, event.offset)
        }
      }
    }
}
