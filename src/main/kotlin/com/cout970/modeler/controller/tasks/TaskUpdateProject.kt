package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.project.ProjectProperties

/**
 * Created by cout970 on 2017/07/17.
 */
class TaskUpdateProject(
        val oldProjectProperties: ProjectProperties,
        val newProjectProperties: ProjectProperties,
        val oldModel: IModel,
        val newModel: IModel,
        val oldAnimation: IAnimation,
        val newAnimation: IAnimation
) : IUndoableTask {

    override fun run(state: Program) {
        state.projectManager.loadProjectProperties(newProjectProperties)
        state.projectManager.updateModel(newModel)
        state.projectManager.updateAnimation(newAnimation)
        state.windowHandler.updateTitle(newProjectProperties.name)

        newModel.materialMap.forEach { _, u -> u.loadTexture(state.resourceLoader) }
    }

    override fun undo(state: Program) {
        state.projectManager.loadProjectProperties(oldProjectProperties)
        state.projectManager.updateModel(oldModel)
        state.projectManager.updateAnimation(oldAnimation)
        state.windowHandler.updateTitle(oldProjectProperties.name)

        oldModel.materialMap.forEach { _, u -> u.loadTexture(state.resourceLoader) }
    }
}