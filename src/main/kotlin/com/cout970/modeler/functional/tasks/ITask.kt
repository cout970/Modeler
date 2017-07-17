package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState

/**
 * Created by cout970 on 2017/07/17.
 */
interface ITask {
    fun run(state: ProgramState)
}

interface IUndoableTask : ITask {
    fun undo(state: ProgramState)
}