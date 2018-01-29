package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateModel
import com.cout970.modeler.controller.tasks.TaskUpdateModelSelection
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.util.asNullable
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/20.
 */

@UseCase("tree.view.delete.item")
fun deleteListItem(component: Component, model: IModel, projectManager: ProjectManager): ITask {
    return component.asNullable()
            .map { it.metadata["ref"] }
            .flatMap { it as? IObjectRef }
            .map { delete(it, model, projectManager) }
            .getOr(TaskNone)
}

private fun delete(ref: IObjectRef, model: IModel, projectManager: ProjectManager): ITask {
    val selection = Selection(
            SelectionTarget.MODEL,
            SelectionType.OBJECT,
            listOf(ref as IRef)
    )
    val texSel = projectManager.textureSelectionHandler.getSelection()

    return deleteSelectionInModel(selection.asNullable(), texSel, model)
}

@UseCase("tree.view.hide.item")
fun hideListItem(component: Component, model: IModel): ITask {
    return component.asNullable()
            .map { it.metadata["ref"] }
            .flatMap { it as? IObjectRef }
            .map { ref ->
                val newModel = model.modifyObjects({ it == ref }) { _, obj -> obj.withVisibility(false) }
                TaskUpdateModel(oldModel = model, newModel = newModel) as ITask
            }
            .getOr(TaskNone)
}

@UseCase("tree.view.show.item")
fun showListItem(component: Component, model: IModel): ITask {
    return component.asNullable()
            .map { it.metadata["ref"] }
            .flatMap { it as? IObjectRef }
            .map { ref ->
                val newModel = model.modifyObjects({ it == ref }) { _, obj -> obj.withVisibility(true) }
                TaskUpdateModel(oldModel = model, newModel = newModel) as ITask
            }
            .getOr(TaskNone)
}

@UseCase("tree.view.select")
fun selectListItem(component: Component, input: IInput, modelAccessor: IModelAccessor): ITask {
    val selection = modelAccessor.modelSelection

    return component.asNullable()
            .map { it.metadata["ref"] }
            .flatMap { it as? IObjectRef }
            .map { ref ->
                val multiSelection = Config.keyBindings.multipleSelection.check(input)
                val sel = modelAccessor.modelSelectionHandler.updateSelection(selection, multiSelection, ref)
                TaskUpdateModelSelection(
                        oldSelection = selection,
                        newSelection = sel
                ) as ITask
            }
            .getOr(TaskNone)
}


@UseCase("model.toggle.visibility")
fun toggleListItemVisibility(model: IModel, modelAccessor: IModelAccessor): ITask {
    return modelAccessor.modelSelection
            .map { it to it.objects.first() }
            .map { toggle(it, model) }
            .getOr(TaskNone)
}

private fun toggle(pair: Pair<ISelection, IObjectRef>, model: IModel): ITask {
    val (sel, ref) = pair
    val target = !model.getObject(ref).visible

    val newModel = model.modifyObjects(sel.objects.toSet()) { _, obj -> obj.withVisibility(target) }

    return TaskUpdateModel(model, newModel)
}