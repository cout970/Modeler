package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
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

    override fun run(state: Program) {
        state.projectManager.loadProjectProperties(newProjectProperties)
        state.projectManager.updateModel(newModel)
        state.windowHandler.updateTitle(newProjectProperties.name)

        newModel.materialMap.forEach { _, u -> u.loadTexture(state.resourceLoader) }
        state.gui.state.reset()
    }

    override fun undo(state: Program) {
        state.projectManager.loadProjectProperties(oldProjectProperties)
        state.projectManager.updateModel(oldModel)
        state.windowHandler.updateTitle(oldProjectProperties.name)

        oldModel.materialMap.forEach { _, u -> u.loadTexture(state.resourceLoader) }
        state.gui.state.reset()
    }
}