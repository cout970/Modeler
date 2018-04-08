package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.helpers.DeletionHelper
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.selection.*
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.helpers.TransformationHelper
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.getOr

/**
 * Created by cout970 on 2017/07/19.
 */

@UseCase("model.selection.delete")
private fun deleteSelection(model: IModel, projectManager: ProjectManager): ITask {
    val modSel = projectManager.modelSelectionHandler.getSelection()
    val texSel = projectManager.textureSelectionHandler.getSelection()

    return modSel.map { sel ->
        val newModel = DeletionHelper.delete(model, sel)

        TaskUpdateModelAndUnselect(
                oldModel = model,
                newModel = newModel,
                oldModelSelection = modSel,
                oldTextureSelection = texSel
        )
    }.getOr(TaskNone)
}


@UseCase("model.selection.copy")
private fun copySelection(accessor: IModelAccessor, clipboard: IClipboard): ITask {
    val model = accessor.model
    val selection = accessor.modelSelection

    selection.ifNotNull {
        return TaskUpdateClipboard(clipboard, Clipboard(model, it))
    }
    return TaskNone
}

@UseCase("model.selection.paste")
private fun pasteSelection(accessor: IModelAccessor, clipboard: IClipboard): ITask {
    val model = accessor.model
    val selection = accessor.modelSelection

    if (clipboard != ClipboardNone) {
        val saveSelection = clipboard.selection
        if (saveSelection.selectionTarget == SelectionTarget.MODEL) {
            return paste(saveSelection, selection, model, clipboard)
        }
    }
    return TaskNone
}

@UseCase("model.selection.cut")
private fun cutSelection(accessor: IModelAccessor, clipboard: IClipboard): ITask {
    val model = accessor.model
    val selection = accessor.modelSelection

    selection.ifNotNull { sel ->
        val newModel = DeletionHelper.delete(model, sel)
        return TaskChain(listOf(
                TaskUpdateClipboard(oldClipboard = clipboard, newClipboard = Clipboard(model, sel)),
                TaskUpdateModel(oldModel = model, newModel = newModel),
                TaskUpdateModelSelection(
                        sel.asNullable(),
                        Nullable.castNull()
                ),
                TaskUpdateTextureSelection(
                        sel.asNullable(),
                        Nullable.castNull()
                )
        ))
    }
    return TaskNone
}

private fun paste(saveSelection: ISelection, oldSelection: Nullable<ISelection>, model: IModel,
                  clipboard: IClipboard): ITask = when (saveSelection.selectionType) {
    SelectionType.OBJECT -> {

        val selectedObjects = clipboard.model.getSelectedObjects(saveSelection).map { it.makeCopy() }
        val newModel = model.addObjects(selectedObjects)
        val selRefs = selectedObjects.map { ObjectRef(it.id) }

        val newSelection = Selection(SelectionTarget.MODEL, SelectionType.OBJECT, selRefs)

        TaskChain(listOf(
                TaskUpdateModel(oldModel = model, newModel = newModel),
                TaskUpdateModelSelection(oldSelection = oldSelection, newSelection = newSelection.asNullable())
        ))
    }
    SelectionType.FACE -> TaskNone // TODO Implement face Paste
    SelectionType.EDGE, SelectionType.VERTEX -> TaskNone // You can't paste vertex or edges
}