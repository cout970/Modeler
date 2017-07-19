package com.cout970.modeler.functional.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.tool.EditTool
import com.cout970.modeler.functional.injection.Inject
import com.cout970.modeler.functional.tasks.*

/**
 * Created by cout970 on 2017/07/19.
 */
class Delete : IUseCase {

    override val key: String = "model.selection.delete"

    @Inject lateinit var model: IModel
    @Inject var selection: ISelection? = null

    override fun createTask(): ITask {
        selection?.let { sel ->
            val newModel = EditTool.delete(model, sel)
            return TaskChain(listOf(
                    TaskUpdateModel(oldModel = model, newModel = newModel),
                    TaskUpdateSelection(selection, Selection(SelectionTarget.MODEL, SelectionType.OBJECT, emptyList()))
            ))
        }
        return TaskNone
    }
}