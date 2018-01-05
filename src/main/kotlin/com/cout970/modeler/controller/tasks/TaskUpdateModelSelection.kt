package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.util.Nullable

/**
 * Created by cout970 on 2017/07/19.
 */
class TaskUpdateModelSelection(val oldSelection: Nullable<ISelection>,
                               val newSelection: Nullable<ISelection>) : IUndoableTask {

    override fun run(state: Program) {
        state.gui.modelAccessor.modelSelectionHandler.setSelection(newSelection)
    }

    override fun undo(state: Program) {
        state.gui.modelAccessor.modelSelectionHandler.setSelection(oldSelection)
    }
}