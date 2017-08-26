package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program

/**
 * Created by cout970 on 2017/07/19.
 */
class TaskCallback(val callback: ((ITask) -> Unit) -> Unit) : ITask {

    override fun run(state: Program) {
        callback {
            state.taskHistory.processTask(it)
        }
    }
}