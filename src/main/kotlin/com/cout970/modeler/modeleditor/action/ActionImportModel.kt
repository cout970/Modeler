package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.log.print
import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.ModelController
import com.cout970.modeler.modeleditor.selection.SelectionNone

/**
 * Created by cout970 on 2017/01/02.
 */
class ActionImportModel(val modelController: ModelController, val path: String, val function: () -> Model) : IAction {

    val oldModel = modelController.model

    override fun run() {
        try {
            val newModel = function()
            modelController.selectionManager.selection = SelectionNone
            modelController.updateModel(newModel)
        } catch(e: Exception) {
            e.print()
        }
    }

    override fun undo() {
        modelController.updateModel(oldModel)
    }

    override fun toString(): String {
        return "ActionImportModel(path='$path')"
    }
}