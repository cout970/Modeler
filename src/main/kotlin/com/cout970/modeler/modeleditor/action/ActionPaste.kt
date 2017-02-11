package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Selection
import com.cout970.modeler.modeleditor.ModelEditor

/**
 * Created by cout970 on 2016/12/09.
 */
class ActionPaste(val selection: Selection, val copiedModel: Model, val modelEditor: ModelEditor) : IAction {

    //the model when the action is executed
    val model = modelEditor.model

    override fun run() {
        val newModel = modelEditor.inserter.paste(model, copiedModel, selection)
        modelEditor.updateModel(newModel)
    }

    override fun undo() {
        modelEditor.updateModel(model)
    }

    override fun toString(): String {
        return "ActionPaste(selection=$selection, model_when_copied=$copiedModel, model_when_pasted=$model, modelController=$modelEditor)"
    }
}