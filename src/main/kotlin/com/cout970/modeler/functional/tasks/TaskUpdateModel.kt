package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState
import com.cout970.modeler.api.model.IModel

/**
 * Created by cout970 on 2017/07/17.
 */
class TaskUpdateModel(val oldModel: IModel, val newModel: IModel) : IUndoableTask {

    override fun run(state: ProgramState) {
        state.projectManager.updateModel(newModel)
    }

    override fun undo(state: ProgramState) {
        state.projectManager.updateModel(oldModel)
    }
}