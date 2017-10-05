package com.example.datalogger.impl

import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement, Row}
import com.example.datalogger.api.{AddMeasure, Measure}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, ReadSideProcessor}
import org.joda.time.DateTime
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future, Promise}

/**
  * This event processor writes processed events into some Cassandra table, in order to persist
  * the event into a more convenient way.
  *
  * @param session
  * @param readSide
  * @param ctx
  * @author jazumaquero
  */
class MeasureEventProcessor(session: CassandraSession, readSide: CassandraReadSide)
                           (implicit ctx: ExecutionContext) extends ReadSideProcessor[MeasureEvent] {

  import MeasureEventProcessor._

  /** Just a simple logger **/
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  override def buildHandler() = {
    readSide.builder[MeasureEvent]("measureEventProcessor")
      .setGlobalPrepare(createMeasureTable)
      .setPrepare(tag => prepareWriteMeasure())
      .setEventHandler[AddMeasureEvent](processMeasureEvent)
      .build
  }

  override def aggregateTags = MeasureEvent.Tag.allTags

  /**
    * This handler must be run on raise in order to ensure that table is created just before start
    * writing.
    *
    * @return
    */
  protected def createMeasureTable(): Future[Done] = {
    logger.info("Creating measure table on Cassandra")
    session.executeCreateTable(CreateMeasureTableStatement)
  }

  private val writeMeasurePromise = Promise[PreparedStatement]

  private def writeMeasure: Future[PreparedStatement] = writeMeasurePromise.future

  /**
    * Prepared statement that will be used to insert data into the table.
    *
    * @return
    */
  protected def prepareWriteMeasure(): Future[Done] = {
    val f = session.prepare(InsertIntoMeasureTablePreparedStatement)
    writeMeasurePromise.completeWith(f)
    f.map(_ => Done)
  }

  /**
    * This event handler transform some stream of {@link AddMeasureEvent} into a future list of
    * {@link BoundStatement}, that will be ready to execute the insertions of the events into table
    * just by using the registered prepared statement.
    *
    * @param eventElement
    * @return
    */
  private def processMeasureEvent(eventElement: EventStreamElement[AddMeasureEvent]): Future[List[BoundStatement]] = {
    writeMeasure.map { preparedStatement =>
      val measure = eventElement.event.measure
      measure.metrics map { metric =>
        val bindWriteMeasure = preparedStatement.bind()
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

/**
  * Includes all queries that may be used either by the {@link MeasureEventProcessor} or by any
  * instance of the {@link CassandraSession} in order to execute some query.
  *
  * @author jazumaquero
  */
object MeasureEventProcessor {
  /** Name of the table where measures are going to be stored. **/
  val MeasureTable: String = "measures"

  /** Statement used to create the measurement table. **/
  val CreateMeasureTableStatement: String =
    s"""CREATE TABLE IF NOT EXISTS $MeasureTable
       | (id text, name text, tstamp timestamp, value double, last_updated timestamp,
       | PRIMARY KEY ((id, name), tstamp)
       | )
       |""".stripMargin

  /** Statement used to insert a measure into the table **/
  val InsertIntoMeasureTablePreparedStatement: String =
    s"""INSERT INTO $MeasureTable
       |(id , name, tstamp, value, last_updated)
       |VALUES (?, ?, ?, ?, ?)
       |""".stripMargin

  /** *
    * Statement used to select all measure from some metrics into a timerange
    *
    * @param id
    * @param name
    * @param init
    * @param end
    * @return
    */
  def selectMeasuresFromTimeRangeStatement(id: String, name: String, init: String, end: String): String =
    s"""SELECT id , name, tstamp, value, last_updated
       |FROM $MeasureTable
       |WHERE id= '$id' AND name = '$name' AND tstamp >= '$init' AND tstamp <= '$end'
     """.stripMargin

  /** *
    * Statement used to select all measure from some metrics into a timerange
    *
    * @param id
    * @param name
    * @param init
    * @param end
    * @return
    */
  def selectMeasuresFromTimeRangeStatement(id: String, name: String, init: DateTime, end: DateTime): String = {
    val pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    selectMeasuresFromTimeRangeStatement(id, name, init.toString(pattern), end.toString(pattern))
  }

  /**
    *
    * @param row
    * @return
    */
  def getAddMeasure(row: Row): AddMeasure = {
    val id = row.getString("id")
    val lastUpdated = new DateTime(row.getTimestamp("last_updated"))
    val name = row.getString("name")
    val value = row.getDouble("value")
    val tstamp = new DateTime(row.getTimestamp("tstamp"))
    AddMeasure(id, lastUpdated, List(Measure(name, value, tstamp)))
  }
}