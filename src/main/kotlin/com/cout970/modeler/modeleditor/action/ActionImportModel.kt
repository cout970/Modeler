package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.log.print
import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.resource.ResourceLoader
import javax.swing.JOptionPane

/**
 * Created by cout970 on 2017/01/02.
 */
class ActionImportModel(val modelEditor: ModelEditor,
                        val resourceLoader: ResourceLoader,
                        val path: String,
                        val function: () -> Model) : IAction {

    val oldModel = modelEditor.model

    override fun run() {
        try {
            val newModel = function()
            modelEditor.selectionManager.clearModelSelection()
            newModel.resources.materials.distinct().forEach {
                it.loadTexture(resourceLoader)
            }
            modelEditor.updateModel(newModel)
        } catch(e: Exception) {
            e.print()
            JOptionPane.showMessageDialog(null, "Error importing model: \n$e")
        }
    }

    override fun undo() {
        modelEditor.updateModel(oldModel)
    }

    override fun toString(): String {
        return "ActionImportModel(path='$path')"
    }
}