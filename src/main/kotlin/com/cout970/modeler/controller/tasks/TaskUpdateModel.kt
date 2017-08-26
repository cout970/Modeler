package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.IModel

/**
 * Created by cout970 on 2017/07/17.
 */
class TaskUpdateModel(val oldModel: IModel, val newModel: IModel) : IUndoableTask {

    override fun run(state: Program) {
        state.projectManager.updateModel(newModel)
    }

    override fun undo(state: Program) {
        state.projectManager.updateModel(oldModel)
    }
}