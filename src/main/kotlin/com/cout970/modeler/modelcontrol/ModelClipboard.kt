package com.cout970.modeler.modelcontrol

import com.cout970.modeler.model.Model
import com.cout970.modeler.modelcontrol.action.ActionDelete
import com.cout970.modeler.modelcontrol.action.ActionPaste
import com.cout970.modeler.modelcontrol.selection.Selection
import com.cout970.modeler.modelcontrol.selection.SelectionMode
import com.cout970.modeler.modelcontrol.selection.SelectionNone

/**
 * Created by cout970 on 2016/12/08.
 */
class ModelClipboard(val modelController: ModelController) {

    var content: Pair<Selection, Model>? = null

    fun copy() {
        if (modelController.selectionManager.selection.mode != SelectionMode.VERTEX && modelController.selectionManager.selection != SelectionNone) {
            content = modelController.selectionManager.selection to modelController.model.copy()
        }
    }

    fun cut() {
        if (modelController.selectionManager.selection.mode != SelectionMode.VERTEX && modelController.selectionManager.selection != SelectionNone) {
            content = modelController.selectionManager.selection to modelController.model.copy()
            modelController.historyRecord.doAction(ActionDelete(modelController.selectionManager.selection, modelController))
        }
    }

    fun paste() {
        content?.let {
            modelController.historyRecord.doAction(ActionPaste(it.first, it.second, modelController))
        }
    }
}