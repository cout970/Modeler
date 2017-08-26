package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program

/**
 * Created by cout970 on 2017/07/19.
 */
class TaskChain(val taskList: List<ITask>) : IUndoableTask {

    override fun run(state: Program) {
        taskList.forEach { it.run(state) }
    }

    override fun undo(state: Program) {
        taskList.reversed()
                .filterIsInstance<IUndoableTask>()
                .forEach { it.undo(state) }
    }
}