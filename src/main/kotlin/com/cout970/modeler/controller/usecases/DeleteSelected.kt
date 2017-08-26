package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.tool.EditTool
import org.funktionale.option.Option
import org.funktionale.option.getOrElse

/**
 * Created by cout970 on 2017/07/19.
 */
class DeleteSelected : IUseCase {

    override val key: String = "model.selection.delete"

    @Inject lateinit var model: IModel
    @Inject lateinit var selection: Option<ISelection>

    override fun createTask(): ITask {
        return selection.map { sel ->
            val newModel = EditTool.delete(model, sel)
            TaskChain(listOf(
                    TaskUpdateModel(oldModel = model, newModel = newModel),
                    TaskUpdateSelection(sel, Selection(SelectionTarget.MODEL, SelectionType.OBJECT, emptyList()))
            ))
        }.getOrElse { TaskNone }
    }
}