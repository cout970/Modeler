package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskUpdateSelection
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.util.Nullable

/**
 * Created by cout970 on 2017/10/01.
 */

class SelectAll : IUseCase {

    override val key: String = "model.select.all"

    @Inject lateinit var model: IModel
    @Inject lateinit var selection: Nullable<ISelection>

    override fun createTask(): ITask {
        val newSelection = Selection(SelectionTarget.MODEL, SelectionType.OBJECT, model.objectRefs)
        return TaskUpdateSelection(
                oldSelection = selection.getOrNull(),
                newSelection = newSelection
        )
    }
}