package com.example.datareader.impl

import akka.{Done, NotUsed}
import com.example.datareader.api.{DataReaderService, GetReadMeasure, ReadMeasure}
import com.lightbend.lagom.javadsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContext

/**
  * Implementation of the {@link DataReaderService}.
  *
  * @author durdiales
  */
class DataReaderServiceImpl(persistentEntityRegistry: PersistentEntityRegistry,
  db: CassandraSession)(implicit ctx: ExecutionContext) extends DataReaderService {
  /** Just a simple logger. **/
  private final val logger: Logger = LoggerFactory.getLogger(classOf[DataReaderServiceImpl])

  persistentEntityRegistry.register(new ReadMeasureEntity)

  override def getReadMeasure(id: String): ServiceCall[NotUsed, ReadMeasure] = {
      measureEntityRef(id).ask[GetReadMeasure](GetReadMeasure())
        .map(_.readReadMeasure.getOrElse(throw new NotFound(s"measure $id not found")))
  }

  private def measureEntityRef(measureId: String) =
    persistentEntityRegistry.refFor[ReadMeasureEntity](measureId)
}
