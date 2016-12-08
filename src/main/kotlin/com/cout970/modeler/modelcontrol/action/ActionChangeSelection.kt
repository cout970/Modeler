package com.cout970.modeler.modelcontrol.action

import com.cout970.modeler.modelcontrol.ModelController
import com.cout970.modeler.modelcontrol.selection.Selection

/**
 * Created by cout970 on 2016/12/08.
 */
data class ActionChangeSelection(val oldSelection: Selection, val newSelection: Selection, val modelController: ModelController) : IAction {

    override fun run() {
        modelController.selectionManager.selection = newSelection
        modelController.modelUpdate = true
    }

    override fun undo() {
        modelController.selectionManager.selection = oldSelection
        modelController.modelUpdate = true
    }

    override fun toString(): String {
        return "ActionChangeSelection(oldSelection=$oldSelection, newSelection=$newSelection)"
    }
}
