package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskUpdateModelSelection
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.util.asNullable

/**
 * Created by cout970 on 2017/10/01.
 */

@UseCase("model.select.all")
private fun selectAll(model: IModel, programState: IProgramState): ITask {
    val newSelection = Selection(SelectionTarget.MODEL, SelectionType.OBJECT, model.objectRefs)

    return TaskUpdateModelSelection(
            oldSelection = programState.modelSelection,
            newSelection = newSelection.asNullable()
    )
}