package com.cout970.modeler.core.record.action

import com.cout970.modeler.core.log.print

/**
 * Created by cout970 on 2017/01/02.
 */
class ActionImportModel(
        val modelEditor: com.cout970.modeler.to_redo.modeleditor.ModelEditor,
        val resourceLoader: com.cout970.modeler.core.resource.ResourceLoader,
        val path: String,
        val function: () -> com.cout970.modeler.to_redo.model.Model
) : IAction {

    val oldModel = modelEditor.model

    override fun run() {
        try {
            val newModel = function()
            modelEditor.selectionManager.clearSelection()
            newModel.resources.reloadResources(resourceLoader)
            modelEditor.updateModel(newModel)
        } catch(e: Exception) {
            e.print()
            javax.swing.JOptionPane.showMessageDialog(null, "Error importing model: \n$e")
        }
    }

    override fun undo() {
        modelEditor.updateModel(oldModel)
    }

    override fun toString(): String {
        return "ActionImportModel(path='$path')"
    }
}