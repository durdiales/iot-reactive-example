/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.example.datareader.api

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

import org.joda.time.DateTime
import org.joda.time.DateTime.now

final case class Measure(name: String, value: Double, t: DateTime = now())

sealed trait ReadMeasureCommand

case class ReadMeasure(id: String, tstamp: DateTime, metrics: List[Measure]) extends ReadMeasureCommand

case class GetReadMeasure() extends PersistentEntity.ReplyType[GetReadMeasureReply] with ReadMeasureCommand

case class GetReadMeasureReply(readReadMeasure: Option[ReadMeasure])

