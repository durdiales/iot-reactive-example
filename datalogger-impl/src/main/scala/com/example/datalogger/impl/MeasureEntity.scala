package com.example.datalogger.impl

import akka.Done
import com.example.datalogger.api.{AddMeasure, MeasureCommand}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

final class MeasureEntity extends PersistentEntity {
  override type Command = MeasureCommand
  override type Event = MeasureEvent
  override type State = MeasureState

  override def initialState = MeasureState.empty

  override def behavior: Behavior = Actions()
    .onCommand[AddMeasure, Done] {
    case (addMeasure, ctx, state) => {
      ctx.thenPersist(AddMeasureEvent(addMeasure.asInstanceOf[AddMeasure])) { _ =>
        ctx.reply(Done)
      }
    }
  }.onEvent {
    case (AddMeasureEvent(measure), state) => MeasureState(Some(measure), logged = true)
  }
}
