package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.util.Nullable

class TaskUpdateTextureSelection (val oldSelection: Nullable<ISelection>,
                                  val newSelection: Nullable<ISelection>) : IUndoableTask {

    override fun run(state: Program) {
        state.gui.modelAccessor.textureSelectionHandler.setSelection(newSelection)
    }

    override fun undo(state: Program) {
        state.gui.modelAccessor.textureSelectionHandler.setSelection(oldSelection)
    }
}