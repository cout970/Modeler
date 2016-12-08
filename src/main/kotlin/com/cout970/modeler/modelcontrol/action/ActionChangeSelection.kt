package com.cout970.modeler.modelcontrol.action

import com.cout970.modeler.modelcontrol.Selection
import com.cout970.modeler.modelcontrol.SelectionManager

/**
 * Created by cout970 on 2016/12/08.
 */
data class ActionChangeSelection(val oldSelection: Selection, val newSelection: Selection, val selectionManager: SelectionManager, val update: () -> Unit) : IAction {

    override fun run() {
        selectionManager.selection = newSelection
        update()
    }

    override fun undo() {
        selectionManager.selection = oldSelection
        update()
    }

    override fun toString(): String {
        return "ActionChangeSelection(oldSelection=$oldSelection, newSelection=$newSelection)"
    }
}
