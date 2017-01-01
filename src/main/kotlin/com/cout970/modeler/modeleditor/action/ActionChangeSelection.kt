package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.modeleditor.ModelController
import com.cout970.modeler.modeleditor.selection.Selection

/**
 * Created by cout970 on 2016/12/08.
 */
data class ActionChangeSelection(val oldSelection: Selection, val newSelection: Selection, val modelController: ModelController) : IAction {

    override fun run() {
        modelController.selectionManager.selection = newSelection
    }

    override fun undo() {
        modelController.selectionManager.selection = oldSelection
    }

    override fun toString(): String {
        return "ActionChangeSelection(oldSelection=$oldSelection, newSelection=$newSelection)"
    }
}
