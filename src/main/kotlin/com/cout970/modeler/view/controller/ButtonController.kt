package com.cout970.modeler.view.controller

import com.cout970.modeler.log.Level
import com.cout970.modeler.log.log
import com.cout970.modeler.modeleditor.ModelController
import com.cout970.modeler.modeleditor.selection.SelectionMode

/**
 * Created by cout970 on 2017/01/19.
 */
class ButtonController(val modelController: ModelController, val sceneController: SceneController) {

    fun onClick(id: String) {
        when (id) {
            "menu.select.group" -> modelController.selectionManager.selectionMode = SelectionMode.GROUP
            "menu.select.mesh" -> modelController.selectionManager.selectionMode = SelectionMode.MESH
            "menu.select.quad" -> modelController.selectionManager.selectionMode = SelectionMode.QUAD
            "menu.select.vertex" -> modelController.selectionManager.selectionMode = SelectionMode.VERTEX
            "menu.add.cube" -> modelController.inserter.addCube()
            "menu.add.plane" -> modelController.inserter.addPlane()
            "menu.history.undo" -> modelController.historyRecord.undo()
            "menu.history.redo" -> modelController.historyRecord.redo()
            "menu.clipboard.copy" -> modelController.clipboard.copy()
            "menu.clipboard.cut" -> modelController.clipboard.cut()
            "menu.clipboard.paste" -> modelController.clipboard.paste()
            "menu.cursor.translation" -> sceneController.transformationMode = TransformationMode.TRANSLATION
            "menu.cursor.rotation" -> sceneController.transformationMode = TransformationMode.ROTATION
            "menu.cursor.scale" -> sceneController.transformationMode = TransformationMode.SCALE
        /*
        "menu.texture.import"
        "menu.texture.size"
        "menu.texture.flip.x"
        "menu.texture.flip.y"
         */
            else -> log(Level.ERROR) { "unregistered button ID: $id" }
        }
    }
}