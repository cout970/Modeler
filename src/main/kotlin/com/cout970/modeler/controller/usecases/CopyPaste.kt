package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.selection.*
import com.cout970.modeler.core.tool.EditTool
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.asNullable

/**
 * Created by cout970 on 2017/07/19.
 */

class Copy : IUseCase {

    override val key: String = "model.selection.copy"

    @Inject lateinit var model: IModel
    @Inject lateinit var selection: Nullable<ISelection>
    @Inject lateinit var clipboard: IClipboard

    override fun createTask(): ITask {
        return selection.map { TaskUpdateClipboard(clipboard, Clipboard(model, it)) as ITask }.getOr(TaskNone)
    }
}

class Paste : IUseCase {

    override val key: String = "model.selection.paste"

    @Inject lateinit var model: IModel
    @Inject lateinit var clipboard: IClipboard
    @Inject lateinit var selection: Nullable<ISelection>

    override fun createTask(): ITask {
        if (clipboard != ClipboardNone) {
            val saveSelection = clipboard.selection
            if (saveSelection.selectionTarget == SelectionTarget.MODEL) {
                return paste(saveSelection)
            }
        }
        return TaskNone
    }

    fun paste(saveSelection: ISelection): ITask = when (saveSelection.selectionType) {
        SelectionType.OBJECT -> {
            val selectedObjects = clipboard.model.getSelectedObjects(saveSelection)
            val newModel = model.addObjects(selectedObjects)
            val selRefs = (model.objects.size until model.objects.size + selectedObjects.size)
                    .map { ObjectRef(it) }

            val newSelection = Selection(SelectionTarget.MODEL, SelectionType.OBJECT, selRefs)

            TaskChain(listOf(
                    TaskUpdateModel(oldModel = model, newModel = newModel),
                    TaskUpdateModelSelection(oldSelection = selection, newSelection = newSelection.asNullable())
            ))
        }
        SelectionType.FACE -> TaskNone // TODO Implement face Paste
        SelectionType.EDGE, SelectionType.VERTEX -> TaskNone
    }
}

class Cut : IUseCase {

    override val key: String = "model.selection.cut"

    @Inject lateinit var model: IModel
    @Inject lateinit var selection: Nullable<ISelection>
    @Inject lateinit var clipboard: IClipboard

    override fun createTask(): ITask = selection.map(this::cut).getOr(TaskNone)

    fun cut(sel: ISelection): ITask {
        val newModel = EditTool.delete(model, sel)
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
}