package com.example.datalogger.impl

import akka.Done
import com.example.datalogger.api.{AddMeasure, GetLastMeasure, MeasureCommand}
import com.lightbend.lagom.scaladsl.api.transport.NotFound
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
  }.onReadOnlyCommand[GetLastMeasure.type, AddMeasure] {
    case (_, ctx, state) =>
      if (state.logged)
        ctx.reply(state.command.get.asInstanceOf[AddMeasure])
      else
        throw NotFound("Missing entity!")
  }
}
