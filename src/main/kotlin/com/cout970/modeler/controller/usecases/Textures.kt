package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateModel
import com.cout970.modeler.core.helpers.TransformationHelper
import com.cout970.modeler.core.project.IProgramState


@UseCase("model.texture.split")
private fun splitTextures(model: IModel, state: IProgramState): ITask {
    val sel = state.textureSelection.getOrNull() ?: state.modelSelection.getOrNull() ?: return TaskNone
    val newModel = TransformationHelper.splitTextures(model, sel)
    return TaskUpdateModel(oldModel = model, newModel = newModel)
}

@UseCase("model.texture.scale.up")
private fun scaleTexturesUp(accessor: IProgramState): ITask {
    val (model, sel) = accessor
    val selection = sel.getOrNull() ?: return TaskNone

    val newModel = TransformationHelper.scaleTextures(model, selection, 2f)
    return TaskUpdateModel(oldModel = model, newModel = newModel)
}

@UseCase("model.texture.scale.down")
private fun scaleTexturesDown(accessor: IProgramState): ITask {
    val (model, sel) = accessor
    val selection = sel.getOrNull() ?: return TaskNone

    val newModel = TransformationHelper.scaleTextures(model, selection, 0.5f)
    return TaskUpdateModel(oldModel = model, newModel = newModel)
}