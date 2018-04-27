package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.util.Nullable

class TaskUpdateModelAndUnselect(
        val oldModel: IModel,
        val newModel: IModel,
        val oldModelSelection: Nullable<ISelection>,
        val oldTextureSelection: Nullable<ISelection>
) : IUndoableTask {

    override fun run(state: Program) {
        state.gui.modelAccessor.modelSelectionHandler.clear()
        state.gui.modelAccessor.textureSelectionHandler.clear()
        state.projectManager.updateModel(newModel)
    }

    override fun undo(state: Program) {
        state.projectManager.updateModel(oldModel)
        state.gui.modelAccessor.modelSelectionHandler.setSelection(oldModelSelection)
        state.gui.modelAccessor.textureSelectionHandler.setSelection(oldTextureSelection)
    }
}