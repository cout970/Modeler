package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.selection.Selection

/**
 * Created by cout970 on 2016/12/08.
 */
data class ActionChangeSelection(val oldSelection: Selection, val newSelection: Selection,
                                 val modelEditor: ModelEditor) : IAction {

    override fun run() {
        modelEditor.selectionManager.selection = newSelection
    }

    override fun undo() {
        modelEditor.selectionManager.selection = oldSelection
    }

    override fun toString(): String {
        return "ActionChangeSelection(oldSelection=$oldSelection, newSelection=$newSelection)"
    }
}
