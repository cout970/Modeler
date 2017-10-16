package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.tool.EditTool
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.asNullable

/**
 * Created by cout970 on 2017/07/19.
 */
class DeleteSelected : IUseCase {

    override val key: String = "model.selection.delete"

    @Inject lateinit var model: IModel
    @Inject lateinit var selection: Nullable<ISelection>

    override fun createTask(): ITask = selection.map(this::delete).getOr(TaskNone)

    fun delete(sel: ISelection): ITask {
        val newModel = EditTool.delete(model, sel)

        return TaskChain(listOf(
                TaskUpdateModel(oldModel = model, newModel = newModel),
                TaskUpdateModelSelection(
                        sel.asNullable(),
                        Selection(SelectionTarget.MODEL, SelectionType.OBJECT, emptyList()).asNullable()
                )
        ))
    }
}