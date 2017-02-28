package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.log.print
import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.resource.ResourceLoader
import javax.swing.JOptionPane

/**
 * Created by cout970 on 2017/01/28.
 */
class ActionImportTexture(val modelEditor: ModelEditor,
                          val resourceLoader: ResourceLoader,
                          val path: String,
                          val function: (Model) -> Model) : IAction {

    val oldModel = modelEditor.model

    override fun run() {
        try {
            val newModel = function(oldModel)
            modelEditor.selectionManager.clearSelection()
            newModel.resources.materials.distinct().forEach {
                it.loadTexture(resourceLoader)
            }
            modelEditor.updateModel(newModel)
        } catch(e: Exception) {
            e.print()
            JOptionPane.showMessageDialog(null, "Error importing texture: \n$e")
        }
    }

    override fun undo() {
        modelEditor.updateModel(oldModel)
    }

    override fun toString(): String {
        return "ActionImportTexture(path='$path')"
    }
}