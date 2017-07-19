package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.project.ProjectProperties

/**
 * Created by cout970 on 2017/07/17.
 */
class TaskUpdateProject(
        val oldProjectProperties: ProjectProperties,
        val newProjectProperties: ProjectProperties,
        val oldModel: IModel,
        val newModel: IModel
) : IUndoableTask {

    override fun run(state: ProgramState) {
        state.projectManager.loadProjectProperties(newProjectProperties)
        state.projectManager.updateModel(newModel)
    }

    override fun undo(state: ProgramState) {
        state.projectManager.loadProjectProperties(oldProjectProperties)
        state.projectManager.updateModel(oldModel)
    }
}