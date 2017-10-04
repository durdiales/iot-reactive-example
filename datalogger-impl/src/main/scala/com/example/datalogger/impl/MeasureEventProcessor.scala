package com.example.datalogger.impl

import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, ReadSideProcessor}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future, Promise}

class MeasureEventProcessor(session: CassandraSession, readSide: CassandraReadSide)
                           (implicit ctx: ExecutionContext) extends ReadSideProcessor[MeasureEvent] {

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  override def buildHandler() = {
    readSide.builder[MeasureEvent]("measureEventProcessor")
      .setGlobalPrepare(createMeasureTable)
      .setPrepare(tag => prepareWriteMeasure())
      .setEventHandler[AddMeasureEvent](processMeasureEvent)
      .build
  }

  override def aggregateTags = MeasureEvent.Tag.allTags

  protected def createMeasureTable(): Future[Done] = {
    logger.info("Creating measure table on Cassandra")
    session.executeCreateTable(
      """CREATE TABLE IF NOT EXISTS measures
        | (id text, name text, tstamp timestamp, value double, last_updated timestamp,
        | PRIMARY KEY (id, tstamp, name))
        |""".stripMargin
    )
  }

  private val writeMeasurePromise = Promise[PreparedStatement]

  private def writeMeasure: Future[PreparedStatement] = writeMeasurePromise.future

  protected def prepareWriteMeasure(): Future[Done] = {
    val f = session.prepare("INSERT INTO measures (id , name, tstamp, value, last_updated) VALUES (?, ?, ?, ?, ?)")
    writeMeasurePromise.completeWith(f)
    f.map(_ => Done)
  }

  private def processMeasureEvent(eventElement: EventStreamElement[AddMeasureEvent]): Future[List[BoundStatement]] = {
    writeMeasure.map { ps =>
      val measure = eventElement.event.measure
      measure.metrics map { metric =>
        val bindWriteMeasure = ps.bind()
        bindWriteMeasure.setString("id", measure.id)
        bindWriteMeasure.setString("name", metric.name)
        bindWriteMeasure.setTimestamp("tstamp", metric.t.toDate)
        bindWriteMeasure.setDouble("value", metric.value)
        bindWriteMeasure.setTimestamp("last_updated", measure.tstamp.toDate)
        bindWriteMeasure
      }
    }
  }
}
