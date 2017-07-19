package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState

/**
 * Created by cout970 on 2017/07/19.
 */
class TaskCallback(val callback: ((ITask) -> Unit) -> Unit) : ITask {

    override fun run(state: ProgramState) {
        callback {
            state.taskHistory.processTask(it)
        }
    }
}