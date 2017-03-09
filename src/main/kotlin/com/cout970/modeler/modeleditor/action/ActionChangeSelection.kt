package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.selection.SelectionState

/**
 * Created by cout970 on 2016/12/08.
 */
data class ActionChangeSelection(
        val oldSelectionState: SelectionState,
        val newSelectionState: SelectionState,
        val modelEditor: ModelEditor
) : IAction {

    override fun run() {
        modelEditor.selectionManager.selectionState = newSelectionState
    }

    override fun undo() {
        modelEditor.selectionManager.selectionState = oldSelectionState
    }

    override fun toString(): String {
        return "ActionChangeSelection($newSelectionState)"
    }
}
