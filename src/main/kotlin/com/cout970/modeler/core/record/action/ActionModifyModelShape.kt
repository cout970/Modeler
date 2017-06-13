package com.cout970.modeler.core.record.action

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.ProjectController

/**
 * Created by cout970 on 2016/12/10.
 */
data class ActionModifyModelShape(val projectController: ProjectController, val newModel: IModel) : IAction {

    val model = projectController.project.model

    override fun run() {
        projectController.updateModel(newModel)
    }

    override fun undo() {
        projectController.updateModel(model)
    }

    override fun toString(): String {
        return "ActionModifyModelShape(oldModel=$model, newModel=$newModel)"
    }
}