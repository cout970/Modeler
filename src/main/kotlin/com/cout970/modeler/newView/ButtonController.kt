package com.cout970.modeler.newView

import com.cout970.modeler.log.Level
import com.cout970.modeler.log.log
import com.cout970.modeler.model.AABB
import com.cout970.modeler.model.api.IElementLeaf
import com.cout970.modeler.model.util.getLeafElements
import com.cout970.modeler.model.util.toAABB
import com.cout970.modeler.modeleditor.SelectionManager
import com.cout970.modeler.newView.popup.*
import com.cout970.modeler.project.ProjectManager
import com.cout970.modeler.selection.SelectionMode
import com.cout970.modeler.selection.SelectionTarget
import com.cout970.modeler.util.IPropertyBind
import com.cout970.modeler.util.focus
import com.cout970.modeler.util.show
import java.io.File

/**
 * Created by cout970 on 2017/01/19.
 */
class ButtonController(
        private val projectManager: ProjectManager,
        private val guiInitializer: GuiInitializer
) {

    private val selectionManager: SelectionManager get() = projectManager.modelEditor.selectionManager
    private val historyRecord get() = projectManager.modelEditor.historyRecord
    private val clipboard get() = projectManager.modelEditor.clipboard
    private val rootFrame get() = guiInitializer.root
    private val contentPanel get() = guiInitializer.contentPanel
    private val controllerState get() = guiInitializer.contentPanel.controllerState
    private val modelEditor get() = projectManager.modelEditor

    fun onClick(id: String) {
        when (id) {
            "menu.select.element", "input.select.element" -> {
                selectionManager.selectionMode = SelectionMode.ELEMENT
                selectionManager.clearSelection()
            }
            "menu.select.quad", "input.select.quad" -> {
                selectionManager.selectionMode = SelectionMode.EDIT
                selectionManager.vertexPosTarget = SelectionTarget.QUAD
                selectionManager.clearSelection()
            }
            "menu.select.edge", "input.select.edge" -> {
                selectionManager.selectionMode = SelectionMode.EDIT
                selectionManager.vertexPosTarget = SelectionTarget.EDGE
                selectionManager.clearSelection()
            }
            "menu.select.vertex", "input.select.vertex" -> {
                selectionManager.selectionMode = SelectionMode.EDIT
                selectionManager.vertexPosTarget = SelectionTarget.VERTEX
                selectionManager.clearSelection()
            }
            "menu.add.cube", "input.add.cube" -> modelEditor.addCube()
            "menu.add.plane", "input.add.plane" -> modelEditor.addPlane()
            "menu.history.undo", "top.edit.undo", "input.undo" -> historyRecord.undo()
            "menu.history.redo", "top.edit.redo", "input.redo" -> historyRecord.redo()
            "menu.clipboard.copy", "top.edit.copy", "input.copy" -> clipboard.copy()
            "menu.clipboard.cut", "top.edit.cut", "input.cut" -> clipboard.cut()
            "menu.clipboard.paste", "top.edit.paste", "input.paste" -> clipboard.paste()
            "top.edit.delete", "input.delete" -> clipboard.delete()
            "menu.cursor.translation", "input.cursor.translation" -> controllerState.transformationMode = TransformationMode.TRANSLATION
            "menu.cursor.rotation", "input.cursor.rotation" -> controllerState.transformationMode = TransformationMode.ROTATION
            "menu.cursor.scale", "input.cursor.scale" -> controllerState.transformationMode = TransformationMode.SCALE

            "top.file.new", "input.file.new" -> newProject(projectManager)
            "top.file.open", "input.file.open" -> loadProject(projectManager)
            "top.file.save", "input.file.save" -> saveProject(projectManager)
            "top.file.save_as", "input.file.save_as" -> saveProjectAs(projectManager)
            "top.file.import", "input.file.import" -> showImportModelPopup(projectManager)
            "top.file.export", "input.file.export" -> showExportModelPopup(projectManager)
            "top.file.settings", "input.file.settings" -> Missing("settings")
            "top.file.exit", "input.file.exit" -> guiInitializer.windowHandler.close()

            "top.view.show_left" -> rootFrame.leftBar.isEnabled = !rootFrame.leftBar.isEnabled
            "top.view.show_right" -> rootFrame.rightBar.isEnabled = !rootFrame.rightBar.isEnabled
            "top.view.one_model" -> contentPanel.sceneHandler.setSceneLayout(0, guiInitializer.modelViewTarget,
                    guiInitializer.textureViewTarget)
            "top.view.two_model" -> contentPanel.sceneHandler.setSceneLayout(1, guiInitializer.modelViewTarget,
                    guiInitializer.textureViewTarget)
            "top.view.four_model" -> contentPanel.sceneHandler.setSceneLayout(2, guiInitializer.modelViewTarget,
                    guiInitializer.textureViewTarget)
            "top.view.model_and_texture" -> contentPanel.sceneHandler.setSceneLayout(3, guiInitializer.modelViewTarget,
                    guiInitializer.textureViewTarget)
            "top.view.3_model_1_texture" -> contentPanel.sceneHandler.setSceneLayout(4, guiInitializer.modelViewTarget,
                    guiInitializer.textureViewTarget)

            "menu.texture.import", "input.texture.import" -> importTexture(projectManager)
            "menu.texture.export", "input.texture.export" -> exportTexture(projectManager)
            "menu.texture.split", "input.texture.split" -> modelEditor.editTool.splitTextures(modelEditor)

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
            "menu.texture.show_all_mesh" -> controllerState.showAllMeshUVs
            "menu.aabb.show_aabb" -> controllerState.showBoundingBoxes

            else -> {
                log(Level.ERROR) { "Unregistered toggle button ID: $id" }
                ignore
            }
        }
    }

    fun openSearchBar() {
        guiInitializer.renderManager.guiRenderer.context.focus(rootFrame.searchPanel.searchBar)
        rootFrame.searchPanel.show()
        rootFrame.searchPanel.searchResults.show()
    }
}