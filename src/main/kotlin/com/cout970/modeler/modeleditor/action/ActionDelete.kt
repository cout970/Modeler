package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.model.Selection
import com.cout970.modeler.modeleditor.ModelEditor

/**
 * Created by cout970 on 2016/12/08.
 */
data class ActionDelete(val selection: Selection, val modelEditor: ModelEditor) : IAction {

    val model = modelEditor.model

    override fun run() {
        modelEditor.updateModel(model.delete(selection))
        modelEditor.selectionManager.clearModelSelection()
    }

    override fun undo() {
        modelEditor.updateModel(model)
        modelEditor.selectionManager.modelSelection = selection
    }

    override fun toString(): String {
        return "ActionDelete(selection=$selection, oldModel=$model)"
    }


}