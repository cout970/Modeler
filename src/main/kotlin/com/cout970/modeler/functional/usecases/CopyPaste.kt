package com.cout970.modeler.functional.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.selection.Clipboard
import com.cout970.modeler.core.model.selection.ClipboardNone
import com.cout970.modeler.core.model.selection.IClipboard
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.tool.EditTool
import com.cout970.modeler.functional.injection.Inject
import com.cout970.modeler.functional.tasks.*

/**
 * Created by cout970 on 2017/07/19.
 */

class Copy : IUseCase {

    override val key: String = "model.selection.copy"

    @Inject lateinit var model: IModel
    @Inject var selection: ISelection? = null
    @Inject lateinit var clipboard: IClipboard

    override fun createTask(): ITask {
        selection?.let { sel ->
            return TaskUpdateClipboard(clipboard, Clipboard(model, sel))
        }
        return TaskNone
    }
}

class Paste : IUseCase {

    override val key: String = "model.selection.paste"

    @Inject lateinit var model: IModel
    @Inject lateinit var clipboard: IClipboard

    override fun createTask(): ITask {
        if (clipboard != ClipboardNone) {
            val selection = clipboard.selection
            if (selection.selectionTarget == SelectionTarget.MODEL) {
                when (selection.selectionType) {
                    SelectionType.OBJECT -> {
                        val selectedObjects = clipboard.model.getSelectedObjects(selection)
                        val newModel = model.addObjects(selectedObjects)
                        return TaskUpdateModel(oldModel = model, newModel = newModel)
                    }
                    SelectionType.FACE -> TODO("Implement face Paste")
                    SelectionType.EDGE, SelectionType.VERTEX -> return TaskNone
                }

            }
        }
        return TaskNone
    }
}

class Cut : IUseCase {

    override val key: String = "model.selection.cut"

    @Inject lateinit var model: IModel
    @Inject var selection: ISelection? = null
    @Inject lateinit var clipboard: IClipboard

    override fun createTask(): ITask {
        selection?.let { sel ->
            val newModel = EditTool.delete(model, sel)
            return TaskChain(listOf(
                    TaskUpdateClipboard(oldClipboard = clipboard, newClipboard = Clipboard(model, sel)),
                    TaskUpdateModel(oldModel = model, newModel = newModel),
                    TaskUpdateSelection(selection, Selection(SelectionTarget.MODEL, SelectionType.OBJECT, emptyList()))
            ))
        }
        return TaskNone
    }
}