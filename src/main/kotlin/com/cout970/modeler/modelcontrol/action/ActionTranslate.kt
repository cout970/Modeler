package com.cout970.modeler.modelcontrol.action

import com.cout970.modeler.model.Model
import com.cout970.modeler.modelcontrol.ModelController

/**
 * Created by cout970 on 2016/12/10.
 */
data class ActionTranslate(val modelController: ModelController, val newModel: Model) : IAction {

    val model = modelController.model
    val selection = modelController.selectionManager.selection

    override fun run() {
        modelController.updateModel(newModel)
        modelController.modelUpdate = true
    }

    override fun undo() {
        modelController.updateModel(model)
        modelController.modelUpdate = true
    }

    override fun toString(): String {
        return "ActionTranslate(oldModel=$model, newModel=$newModel, selection=$selection)"
    }
}