package com.cout970.modeler.functional.usecases

import com.cout970.modeler.ProgramState
import com.cout970.modeler.functional.tasks.ITask
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/17.
 */

interface IUserEvent

interface IEventProcessor<in T : IUserEvent> {
    fun processEvent(event: T): ITask
}

interface IUseCase<T : IUserEvent> {

    val key: String
    val processor: IEventProcessor<T>

    fun buildEvent(state: ProgramState, caller: Component?): T
}