package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program

/**
 * Created by cout970 on 2017/07/17.
 */
interface ITask {
    fun run(state: Program)
}

interface IUndoableTask : ITask {
    fun undo(state: Program)
}