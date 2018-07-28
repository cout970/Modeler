package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.IModel

/**
 * Created by cout970 on 2017/07/17.
 */
class TaskUpdateModel(val oldModel: IModel, val newModel: IModel) : IUndoableTask {

    override fun run(state: Program) {
        state.gui.state.tmpModel = null
        state.projectManager.updateModel(newModel)
        state.gui.listeners.onAnimatorChange(state.gui.animator)
    }

    override fun undo(state: Program) {
        state.gui.state.tmpModel = null
        state.projectManager.updateModel(oldModel)
        state.gui.listeners.onAnimatorChange(state.gui.animator)
    }
}