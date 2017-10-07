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
import org.funktionale.option.Option
import org.funktionale.option.getOrElse

/**
 * Created by cout970 on 2017/07/19.
 */

class Copy : IUseCase {

    override val key: String = "model.selection.copy"

    @Inject lateinit var model: IModel
    @Inject lateinit var selection: Option<ISelection>
    @Inject lateinit var clipboard: IClipboard

    override fun createTask(): ITask {
        return selection.map { TaskUpdateClipboard(clipboard, Clipboard(model, it)) }.getOrElse { TaskNone }
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
                return when (saveSelection.selectionType) {
                    SelectionType.OBJECT -> {
                        val selectedObjects = clipboard.model.getSelectedObjects(saveSelection)
                        val newModel = model.addObjects(selectedObjects)
                        val selRefs = (model.objects.size until model.objects.size + selectedObjects.size).map {
                            ObjectRef(it)
                        }
                        val newSelection = Selection(SelectionTarget.MODEL, SelectionType.OBJECT, selRefs)

                        TaskChain(listOf(
                                TaskUpdateModel(oldModel = model, newModel = newModel),
                                TaskUpdateSelection(oldSelection = selection.getOrNull(), newSelection = newSelection)
                        ))
                    }
                    SelectionType.FACE -> TODO("Implement face Paste")
                    SelectionType.EDGE, SelectionType.VERTEX -> TaskNone
                }

            }
        }
        return TaskNone
    }
}

class Cut : IUseCase {

    override val key: String = "model.selection.cut"

    @Inject lateinit var model: IModel
    @Inject lateinit var selection: Option<ISelection>
    @Inject lateinit var clipboard: IClipboard

    override fun createTask(): ITask {
        return selection.map { sel ->
            val newModel = EditTool.delete(model, sel)
            TaskChain(listOf(
                    TaskUpdateClipboard(oldClipboard = clipboard, newClipboard = Clipboard(model, sel)),
                    TaskUpdateModel(oldModel = model, newModel = newModel),
                    TaskUpdateSelection(sel, Selection(SelectionTarget.MODEL, SelectionType.OBJECT, emptyList()))
            ))
        }.getOrElse { TaskNone }
    }
}