package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.tool.EditTool
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.asNullable

/**
 * Created by cout970 on 2017/07/19.
 */
class DeleteSelected : IUseCase {

    override val key: String = "model.selection.delete"

    @Inject lateinit var model: IModel
    @Inject lateinit var projectManager: ProjectManager

    override fun createTask(): ITask {
        val modSel = projectManager.modelSelectionHandler.getSelection()
        val texSel = projectManager.textureSelectionHandler.getSelection()
        return delete(modSel, texSel, model)
    }

    fun delete(modSel: Nullable<ISelection>, texSel: Nullable<ISelection>, model: IModel): ITask{
        val modSel2 = modSel.getOrNull() ?: return TaskNone
        val newModel = EditTool.delete(model, modSel2)

        return TaskChain(listOf(
                TaskUpdateModelSelection(
                        oldSelection = modSel2.asNullable(),
                        newSelection = Nullable.castNull()
                ),
                TaskUpdateTextureSelection(
                        oldSelection = texSel,
                        newSelection = Nullable.castNull()
                ),
                TaskUpdateModel(oldModel = model, newModel = newModel)
        ))
    }
}