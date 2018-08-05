package com.cout970.modeler.controller.usecases

import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateModel
import com.cout970.modeler.core.project.IProgramState
import org.liquidengine.legui.component.Component

@UseCase("update.group.transform")
private fun changeGroup(comp: Component, access: IProgramState): ITask {
    val offset = comp.metadata["offset"] as? Float ?: return TaskNone
    val cmd = comp.metadata["command"] as? String ?: return TaskNone
    val text = comp.metadata["content"] as? String ?: return TaskNone

    val model = access.model
    val ref = access.selectedGroup
    val group = model.getGroup(ref)
    val transform = updateTransformation(group.transform, cmd, text, offset) ?: return TaskNone
    val newGroup = group.withTransform(transform)

    if (group.transform == newGroup.transform) return TaskNone

    return TaskUpdateModel(model, model.modifyGroup(newGroup))
}