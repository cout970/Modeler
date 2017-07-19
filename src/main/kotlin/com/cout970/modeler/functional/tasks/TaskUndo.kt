package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState

/**
 * Created by cout970 on 2017/07/19.
 */
object TaskUndo : ITask {

    override fun run(state: ProgramState) {
        state.taskHistory.undo()
    }
}