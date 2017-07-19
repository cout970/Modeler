package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState

/**
 * Created by cout970 on 2017/07/19.
 */
class TaskChain(val taskList: List<ITask>) : IUndoableTask {

    override fun run(state: ProgramState) {
        taskList.forEach { it.run(state) }
    }

    override fun undo(state: ProgramState) {
        taskList.reversed()
                .filterIsInstance<IUndoableTask>()
                .forEach { it.undo(state) }
    }
}