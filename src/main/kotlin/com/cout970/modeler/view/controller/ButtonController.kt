package com.cout970.modeler.view.controller

import com.cout970.modeler.log.Level
import com.cout970.modeler.log.log
import com.cout970.modeler.modeleditor.selection.SelectionManager
import com.cout970.modeler.modeleditor.selection.SelectionMode
import com.cout970.modeler.project.ProjectManager
import com.cout970.modeler.view.UIManager
import com.cout970.modeler.view.popup.*

/**
 * Created by cout970 on 2017/01/19.
 */
class ButtonController(
        private val projectManager: ProjectManager,
        private val uiManager: UIManager
) {

    private val selectionManager: SelectionManager get() = projectManager.modelEditor.selectionManager
    private val inserter get() = projectManager.modelEditor.inserter
    private val historyRecord get() = projectManager.modelEditor.historyRecord
    private val clipboard get() = projectManager.modelEditor.clipboard
    private val sceneController get() = uiManager.sceneController
    private val rootFrame get() = uiManager.rootFrame

    fun onClick(id: String) {
        when (id) {
            "menu.select.group" -> selectionManager.selectionMode = SelectionMode.GROUP
            "menu.select.mesh" -> selectionManager.selectionMode = SelectionMode.MESH
            "menu.select.quad" -> selectionManager.selectionMode = SelectionMode.QUAD
            "menu.select.vertex" -> selectionManager.selectionMode = SelectionMode.VERTEX
            "menu.add.cube" -> inserter.addCube()
            "menu.add.plane" -> inserter.addPlane()
            "menu.history.undo", "top.edit.undo", "input.undo" -> historyRecord.undo()
            "menu.history.redo", "top.edit.redo", "input.redo" -> historyRecord.redo()
            "menu.clipboard.copy", "top.edit.copy", "input.copy" -> clipboard.copy()
            "menu.clipboard.cut", "top.edit.cut", "input.cut" -> clipboard.cut()
            "menu.clipboard.paste", "top.edit.paste", "input.paste" -> clipboard.paste()
            "top.edit.delete", "input.delete" -> clipboard.delete()
            "menu.cursor.translation" -> sceneController.modelTransformationMode = TransformationMode.TRANSLATION
            "menu.cursor.rotation" -> sceneController.modelTransformationMode = TransformationMode.ROTATION
            "menu.cursor.scale" -> sceneController.modelTransformationMode = TransformationMode.SCALE
        /*
            "menu.texture.import"
            "menu.texture.size"
            "menu.texture.flip.x"
            "menu.texture.flip.y"
         */
            "top.file.new" -> newProject(projectManager)
            "top.file.open" -> loadProject(projectManager)
            "top.file.save" -> saveProject(projectManager)
            "top.file.saveas" -> saveProjectAs(projectManager)
            "top.file.import" -> showImportModelPopup(projectManager)
            "top.file.export" -> showExportModelPopup(projectManager)
            "top.file.settings" -> Missing("settings")
            "top.file.exit" -> uiManager.windowHandler.close()

            "top.view.showleft" -> rootFrame.leftBar.isEnabled = !rootFrame.leftBar.isEnabled
            "top.view.showright" -> rootFrame.rightBar.isEnabled = !rootFrame.rightBar.isEnabled
            else -> log(Level.ERROR) { "unregistered button ID: $id" }
        }
    }
}