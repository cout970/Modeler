package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program

/**
 * Created by cout970 on 2017/07/19.
 */
object TaskRedo : ITask {

    override fun run(state: Program) {
        state.taskHistory.redo()
    }
}