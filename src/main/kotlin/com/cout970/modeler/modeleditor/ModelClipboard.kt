package com.cout970.modeler.modeleditor

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Selection
import com.cout970.modeler.model.SelectionNone
import com.cout970.modeler.modeleditor.action.ActionDelete
import com.cout970.modeler.modeleditor.action.ActionPaste

/**
 * Created by cout970 on 2016/12/08.
 */
class ModelClipboard(val modelEditor: ModelEditor) {

    val selectionManager get() = modelEditor.selectionManager
    val historyRecord get() = modelEditor.historyRecord
    var content: Pair<Selection, Model>? = null

    fun copy() {
        if (selectionManager.modelSelection != SelectionNone) {
            content = selectionManager.modelSelection to modelEditor.model.copy()
        }
    }

    fun cut() {
        if (selectionManager.modelSelection != SelectionNone) {
            content = selectionManager.modelSelection to modelEditor.model.copy()
            historyRecord.doAction(ActionDelete(selectionManager.modelSelection, modelEditor))
        }
    }

    fun paste() {
        content?.let {
            historyRecord.doAction(ActionPaste(it.first, it.second, modelEditor))
        }
    }

    fun delete() {
        historyRecord.doAction(ActionDelete(selectionManager.modelSelection, modelEditor))
    }
}