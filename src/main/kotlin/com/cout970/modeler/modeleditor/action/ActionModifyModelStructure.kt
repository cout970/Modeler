package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.ModelEditor

/**
 * Created by cout970 on 2016/12/10.
 */
data class ActionModifyModelStructure(val modelEditor: ModelEditor, val newModel: Model) : IAction {

    val model = modelEditor.model

    override fun run() {
        modelEditor.updateModel(newModel)
        modelEditor.selectionManager.clearSelection()
    }

    override fun undo() {
        modelEditor.updateModel(model)
    }

    override fun toString(): String {
        return "ActionModifyModelStructure(oldModel=$model, newModel=$newModel)"
    }
}