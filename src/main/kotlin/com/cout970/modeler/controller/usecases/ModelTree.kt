package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.helpers.DeletionHelper
import com.cout970.modeler.core.helpers.ModelHelper
import com.cout970.modeler.core.model.getRecursiveChildGroups
import com.cout970.modeler.core.model.getRecursiveChildObjects
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.gui.rcomponents.Slot
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.getOr
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/20.
 */

private fun Component.ref() = asNullable().flatMap { it.metadata["ref"] }

private fun Nullable<Any>.asObjectRef() = flatMap { it as? IObjectRef }
private fun Nullable<Any>.asGroupRef() = flatMap { it as? IGroupRef }

@UseCase("tree.view.select.item")
private fun selectListItem(component: Component, input: IInput, modelAccessor: IModelAccessor): ITask {
    val selection = modelAccessor.modelSelection

    return component.ref().asObjectRef().map { ref ->
        val multiSelection = Config.keyBindings.multipleSelection.check(input)
        val sel = modelAccessor.modelSelectionHandler.updateSelection(selection, multiSelection, ref)

        TaskUpdateModelSelection(
                oldSelection = selection,
                newSelection = sel
        )
    }.getOr(TaskNone)
}

@UseCase("tree.view.delete.item")
private fun deleteListItem(component: Component, modelAccessor: IModelAccessor): ITask {
    return component.ref().asObjectRef().map {
        val (model, modSel, texSel) = modelAccessor
        val newModel = DeletionHelper.delete(model, Selection.of(listOf(it)))

        TaskUpdateModelAndUnselect(
                oldModel = model,
                newModel = newModel,
                oldModelSelection = modSel,
                oldTextureSelection = texSel
        )
    }.getOr(TaskNone)
}

@UseCase("tree.view.hide.item")
private fun hideListItem(component: Component, model: IModel): ITask {
    return component.ref().asObjectRef().map { ref ->
        val newModel = ModelHelper.setObjectVisible(model, ref, false)

        TaskUpdateModel(oldModel = model, newModel = newModel)
    }.getOr(TaskNone)
}

@UseCase("tree.view.show.item")
private fun showListItem(component: Component, model: IModel): ITask {
    return component.ref().asObjectRef().map { ref ->
        val newModel = ModelHelper.setObjectVisible(model, ref, true)

        TaskUpdateModel(oldModel = model, newModel = newModel)
    }.getOr(TaskNone)
}

@UseCase("model.toggle.visibility")
private fun toggleListItemVisibility(model: IModel, modelAccessor: IModelAccessor): ITask {
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

// Groups

@UseCase("tree.view.select.group")
private fun selectListGroup(component: Component, input: IInput, modelAccessor: IModelAccessor): ITask {
    val (model, selection) = modelAccessor

    return component.ref().asGroupRef().map { ref ->
        val multiSelection = Config.keyBindings.multipleSelection.check(input)
        val objs = model.getRecursiveChildObjects(ref)

        val newSel: Nullable<ISelection> = if (multiSelection) {
            modelAccessor.modelSelectionHandler.updateSelection(selection, multiSelection, objs)
        } else {
            Selection.of(objs).asNullable()
        }

        TaskUpdateModelSelection(
                oldSelection = selection,
                newSelection = newSel
        )
    }.getOr(TaskNone)
}

@UseCase("tree.view.delete.group")
private fun deleteListGroup(component: Component, model: IModel, projectManager: ProjectManager): ITask {
    return component.ref().asGroupRef().map { ref ->
        val newModel = model.removeGroup(ref)

        TaskUpdateModel(oldModel = model, newModel = newModel)
    }.getOr(TaskNone)
}

@UseCase("tree.view.hide.group")
private fun hideListGroup(component: Component, model: IModel): ITask {
    return component.ref().asGroupRef().map { ref ->
        val newModel = ModelHelper.setGroupVisible(model, ref, false)
        TaskUpdateModel(oldModel = model, newModel = newModel)
    }.getOr(TaskNone)
}

@UseCase("tree.view.show.group")
private fun showListGroup(component: Component, model: IModel): ITask {
    return component.ref().asGroupRef().map { ref ->
        val newModel = ModelHelper.setGroupVisible(model, ref, true)
        TaskUpdateModel(oldModel = model, newModel = newModel)
    }.getOr(TaskNone)
}

@UseCase("tree.view.move.up.item")
private fun moveItemUp(component: Component, model: IModel): ITask {
    val ref = component.ref().asObjectRef().getOrNull() ?: return TaskNone
    return moveItem(ref, model, true)
}

@UseCase("tree.view.move.down.item")
private fun moveItemDown(component: Component, model: IModel): ITask {
    val ref = component.ref().asObjectRef().getOrNull() ?: return TaskNone
    return moveItem(ref, model, false)
}

@UseCase("model.tree.node.moved")
private fun nodeMoved(model: IModel, component: Component): ITask {

    val parent = component.metadata["parent"] as IGroupRef
    val child = component.metadata["child"] as Slot
    val tree = model.groupTree

    val childGroup = child.group
    val childObj = child.obj

    if (childGroup != null) {
        if (tree.getParent(childGroup) != parent) {

            val newTree = tree.changeParent(childGroup, parent)
            val newModel = model.withGroupTree(newTree.update(model.objectMap.keys))

            return TaskUpdateModel(oldModel = model, newModel = newModel)
        }
    } else if (childObj != null) {
        val oldParent = tree.getGroup(childObj)

        if (oldParent != parent) {
            val newTree = tree.removeObject(oldParent, childObj).addObject(parent, childObj)
            val newModel = model.withGroupTree(newTree.update(model.objectMap.keys))

            return TaskUpdateModel(oldModel = model, newModel = newModel)
        }
    }
    return TaskNone
}

private fun moveItem(ref: IObjectRef, model: IModel, up: Boolean): ITask {
    val tree = model.groupTree
    val parent = tree.getGroup(ref)
    val brothers = tree.getObjects(parent)

    return if (ref in brothers) {
        val shift = if (up) -1.5f else 1.5f
        val newBrothers = brothers.sortedBy {
            val index = brothers.indexOf(it)
            index + if (it == ref) shift else 0f
        }
        val newTree = tree.setObjects(parent, newBrothers)
        val newModel = model.withGroupTree(newTree)

        TaskUpdateModel(oldModel = model, newModel = newModel)
    } else {
        TaskNone
    }
}