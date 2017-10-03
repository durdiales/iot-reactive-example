/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.example.datareader.impl

import java.util.Optional

import akka.Done
import com.example.datareader.api.{GetReadMeasure, ReadMeasure, ReadMeasureCommand}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

class ReadMeasureEntity extends PersistentEntity {

    override type Command = ReadMeasureCommand
    override type Event = ReadMeasureEvent
    override type State = Option[ReadMeasure]

    override def initialState = None

    override def behavior: Behavior = Actions()
      .onReadOnlyCommand[GetReadMeasure.type , Option[ReadMeasure]]{
      case (GetReadMeasure, ctx, state) => ctx.reply(state)
    }
  }