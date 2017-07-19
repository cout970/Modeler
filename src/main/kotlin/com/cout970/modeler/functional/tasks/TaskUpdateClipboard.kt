package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState
import com.cout970.modeler.core.model.selection.IClipboard

/**
 * Created by cout970 on 2017/07/19.
 */
class TaskUpdateClipboard(
        val oldClipboard: IClipboard,
        val newClipboard: IClipboard
) : IUndoableTask {
    override fun run(state: ProgramState) {
        state.projectManager.clipboard = newClipboard
    }

    override fun undo(state: ProgramState) {
        state.projectManager.clipboard = oldClipboard
    }
}