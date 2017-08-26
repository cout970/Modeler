package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.selection.ISelection

/**
 * Created by cout970 on 2017/07/19.
 */
class TaskUpdateSelection(val oldSelection: ISelection?, val newSelection: ISelection?) : IUndoableTask {

    override fun run(state: Program) {
        state.gui.selectionHandler.setSelection(newSelection)
    }

    override fun undo(state: Program) {
        state.gui.selectionHandler.setSelection(oldSelection)
    }
}