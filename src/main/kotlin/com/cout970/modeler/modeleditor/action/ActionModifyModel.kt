package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.ModelEditor

/**
 * Created by cout970 on 2016/12/10.
 */
data class ActionModifyModel(val modelEditor: ModelEditor, val newModel: Model) : IAction {

    val model = modelEditor.model
    val selection = modelEditor.selectionManager.selection

    override fun run() {
        modelEditor.updateModel(newModel)
    }

    override fun undo() {
        modelEditor.updateModel(model)
    }

    override fun toString(): String {
        return "ActionTranslate(oldModel=$model, newModel=$newModel, selection=$selection)"
    }
}