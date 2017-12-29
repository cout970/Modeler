package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateModel
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.tool.EditTool


@UseCase("model.texture.split")
fun splitTextures(model: IModel, projectManager: ProjectManager): ITask{
    val selection = projectManager.textureSelectionHandler.getSelection()
    return selection.map { sel ->
        val newModel = EditTool.splitTextures(model, sel)
        TaskUpdateModel(oldModel = model, newModel = newModel) as ITask
    }.getOr(TaskNone)
}