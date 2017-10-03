package com.example.datareader.api

import akka.Done
import com.lightbend.lagom.scaladsl.api._
import com.lightbend.lagom.scaladsl.api.transport.Method

/**
  * This trait defines all common api endpoints required for a data reader service.
  *
  * Initially, we may consider that there is only one endpoint for read measures.
  *
  * @author durdiales
  */
trait DataReaderService extends Service {
  /**
    * Endpoint used to push new measures from some device.
    *
    * @return
    */
  def readMeasure(): ServiceCall[ReadMeasure, Done]



  override def descriptor: Descriptor = {
    import Service._
    named("reader").withCalls(
      restCall(Method.GET, "/measure/:measureId", readMeasure _)
    ).withAutoAcl(true)
  }
}