package com.cout970.modeler.modelcontrol

import com.cout970.modeler.model.Model
import com.cout970.modeler.modelcontrol.action.ActionDelete
import com.cout970.modeler.modelcontrol.selection.Selection

/**
 * Created by cout970 on 2016/12/08.
 */
class ModelClipboard(val modelController: ModelController) {

    var content: Pair<Selection, Model>? = null

    fun copy() {
        content = modelController.selectionManager.selection to modelController.model.copy()
    }

    fun cut() {
        content = modelController.selectionManager.selection to modelController.model.copy()
        modelController.historyRecord.doAction(ActionDelete(modelController.selectionManager.selection, modelController))
    }

    fun paste() {
        
    }
}