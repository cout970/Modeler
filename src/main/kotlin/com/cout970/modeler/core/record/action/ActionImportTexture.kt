package com.cout970.modeler.core.record.action

/**
 * Created by cout970 on 2017/01/28.
 */
//class ActionImportTexture(val modelEditor: com.cout970.modeler.to_redo.modeleditor.ModelEditor,
//                          val resourceLoader: com.cout970.modeler.core.resource.ResourceLoader,
//                          val path: String,
//                          val function: (com.cout970.modeler.to_redo.model.Model) -> com.cout970.modeler.to_redo.model.Model) : IAction {
//
//    val oldModel = modelEditor.model
//
//    override fun run() {
//        try {
//            val newModel = function(oldModel)
//            modelEditor.selectionManager.clearSelection()
//            newModel.resources.reloadResources(resourceLoader)
//            modelEditor.updateModel(newModel)
//        } catch(e: Exception) {
//            e.print()
//            javax.swing.JOptionPane.showMessageDialog(null, "Error importing texture: \n$e")
//        }
//    }
//
//    override fun undo() {
//        modelEditor.updateModel(oldModel)
//    }
//
//    override fun toString(): String {
//        return "ActionImportTexture(path='$path')"
//    }
//}