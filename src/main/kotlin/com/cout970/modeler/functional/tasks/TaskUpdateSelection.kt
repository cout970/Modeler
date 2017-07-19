package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState
import com.cout970.modeler.api.model.selection.ISelection

/**
 * Created by cout970 on 2017/07/19.
 */
class TaskUpdateSelection(val oldSelection: ISelection?, val newSelection: ISelection?) : IUndoableTask {

    override fun run(state: ProgramState) {
        state.gui.selectionHandler.setSelection(newSelection)
    }

    override fun undo(state: ProgramState) {
        state.gui.selectionHandler.setSelection(oldSelection)
    }
}