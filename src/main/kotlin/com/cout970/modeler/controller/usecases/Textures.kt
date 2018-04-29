package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateModel
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.helpers.TransformationHelper
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.util.getOr


@UseCase("model.texture.split")
private fun splitTextures(model: IModel, projectManager: ProjectManager): ITask {
    val selection = projectManager.textureSelectionHandler.getSelection()
    return selection.map { sel ->
        val newModel = TransformationHelper.splitTextures(model, sel)
        TaskUpdateModel(oldModel = model, newModel = newModel)
    }.getOr(TaskNone)
}

@UseCase("model.texture.scale.up")
private fun scaleTexturesUp(accessor: IModelAccessor): ITask {
    val (model, sel) = accessor
    val selection = sel.getOrNull() ?: return TaskNone

    val newModel = TransformationHelper.scaleTextures(model, selection, 2f)
    return TaskUpdateModel(oldModel = model, newModel = newModel)
}

@UseCase("model.texture.scale.down")
private fun scaleTexturesDown(accessor: IModelAccessor): ITask {
    val (model, sel) = accessor
    val selection = sel.getOrNull() ?: return TaskNone

    val newModel = TransformationHelper.scaleTextures(model, selection, 0.5f)
    return TaskUpdateModel(oldModel = model, newModel = newModel)
}