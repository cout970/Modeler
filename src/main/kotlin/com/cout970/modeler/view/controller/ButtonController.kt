package com.cout970.modeler.view.controller

import com.cout970.modeler.log.Level
import com.cout970.modeler.log.log
import com.cout970.modeler.model.AABB
import com.cout970.modeler.model.api.IElementLeaf
import com.cout970.modeler.model.util.getLeafElements
import com.cout970.modeler.model.util.toAABB
import com.cout970.modeler.modeleditor.SelectionManager
import com.cout970.modeler.project.ProjectManager
import com.cout970.modeler.selection.SelectionMode
import com.cout970.modeler.selection.SelectionTarget
import com.cout970.modeler.util.IPropertyBind
import com.cout970.modeler.view.UIManager
import com.cout970.modeler.view.popup.*
import java.io.File

/**
 * Created by cout970 on 2017/01/19.
 */
class ButtonController(
        private val projectManager: ProjectManager,
        private val uiManager: UIManager
) {

    private val selectionManager: SelectionManager get() = projectManager.modelEditor.selectionManager
    private val historyRecord get() = projectManager.modelEditor.historyRecord
    private val clipboard get() = projectManager.modelEditor.clipboard
    private val sceneController get() = uiManager.sceneController
    private val rootFrame get() = uiManager.rootFrame
    private val modelEditor get() = projectManager.modelEditor

    fun onClick(id: String) {
        when (id) {
            "menu.select.element" -> {
                selectionManager.selectionMode = SelectionMode.ELEMENT
                selectionManager.clearSelection()
            }
            "menu.select.quad" -> {
                selectionManager.selectionMode = SelectionMode.EDIT
                selectionManager.vertexPosTarget = SelectionTarget.QUAD
                selectionManager.clearSelection()
            }
            "menu.select.edge" -> {
                selectionManager.selectionMode = SelectionMode.EDIT
                selectionManager.vertexPosTarget = SelectionTarget.EDGE
                selectionManager.clearSelection()
            }
            "menu.select.vertex" -> {
                selectionManager.selectionMode = SelectionMode.EDIT
                selectionManager.vertexPosTarget = SelectionTarget.VERTEX
                selectionManager.clearSelection()
            }
            "menu.add.cube" -> modelEditor.addCube()
            "menu.add.plane" -> modelEditor.addPlane()
            "menu.history.undo", "top.edit.undo", "input.undo" -> historyRecord.undo()
            "menu.history.redo", "top.edit.redo", "input.redo" -> historyRecord.redo()
            "menu.clipboard.copy", "top.edit.copy", "input.copy" -> clipboard.copy()
            "menu.clipboard.cut", "top.edit.cut", "input.cut" -> clipboard.cut()
            "menu.clipboard.paste", "top.edit.paste", "input.paste" -> clipboard.paste()
            "top.edit.delete", "input.delete" -> clipboard.delete()
            "menu.cursor.translation" -> sceneController.transformationMode = TransformationMode.TRANSLATION
            "menu.cursor.rotation" -> sceneController.transformationMode = TransformationMode.ROTATION
            "menu.cursor.scale" -> sceneController.transformationMode = TransformationMode.SCALE

            "top.file.new" -> newProject(projectManager)
            "top.file.open" -> loadProject(projectManager)
            "top.file.save" -> saveProject(projectManager)
            "top.file.save_as" -> saveProjectAs(projectManager)
            "top.file.import" -> showImportModelPopup(projectManager)
            "top.file.export" -> showExportModelPopup(projectManager)
            "top.file.settings" -> Missing("settings")
            "top.file.exit" -> uiManager.windowHandler.close()

            "top.view.show_left" -> rootFrame.leftBar.isEnabled = !rootFrame.leftBar.isEnabled
            "top.view.show_right" -> rootFrame.rightBar.isEnabled = !rootFrame.rightBar.isEnabled
            "top.view.one_model" -> uiManager.showScenes(0)
            "top.view.two_model" -> uiManager.showScenes(1)
            "top.view.four_model" -> uiManager.showScenes(2)
            "top.view.model_and_texture" -> uiManager.showScenes(3)
            "top.view.3_model_1_texture" -> uiManager.showScenes(4)

            "menu.texture.import" -> importTexture(projectManager)
            "menu.texture.export" -> exportTexture(projectManager)
            "menu.texture.split" -> modelEditor.texturizer.splitTextures()

            "menu.aabb.export" -> {
                val aabb = projectManager.project.model.getLeafElements().map(IElementLeaf::toAABB)
                AABB.export(aabb, File("aabb.txt"))
            }

            else -> log(Level.ERROR) { "Unregistered button ID: $id" }
        }
    }

    private var ignore = object : IPropertyBind<Boolean> {
        override fun set(value: Boolean) = Unit
        override fun get(): Boolean = false
    }

    fun getBindProperty(id: String): IPropertyBind<Boolean> {
        return when (id) {
            "menu.texture.show_all_mesh" -> sceneController.showAllMeshUVs
            "menu.aabb.show_aabb" -> sceneController.showBoundingBoxes

            else -> {
                log(Level.ERROR) { "Unregistered toggle button ID: $id" }
                ignore
            }
        }
    }
}