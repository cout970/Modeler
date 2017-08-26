package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.core.model.selection.IClipboard

/**
 * Created by cout970 on 2017/07/19.
 */
class TaskUpdateClipboard(
        val oldClipboard: IClipboard,
        val newClipboard: IClipboard
) : IUndoableTask {
    override fun run(state: Program) {
        state.projectManager.clipboard = newClipboard
    }

    override fun undo(state: Program) {
        state.projectManager.clipboard = oldClipboard
    }
}