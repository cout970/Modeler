package com.cout970.modeler.view.gui.editor.leftpanel

import com.cout970.modeler.util.hide
import com.cout970.modeler.view.GuiResources
import com.cout970.modeler.view.gui.comp.CButton
import com.cout970.modeler.view.gui.comp.CPanel
import com.cout970.modeler.view.gui.editor.leftpanel.editcubepanel.EditCubePanel
import org.joml.Vector2f
import org.liquidengine.legui.icon.ImageIcon

/**
 * Created by cout970 on 2017/06/09.
 */
class LeftPanel : CPanel() {

    val projectControlPanel = ProjectControlPanel()
    val exportPanel = ExportPanel()
    val editCubePanel = EditCubePanel()

    init {
        add(projectControlPanel)
        add(exportPanel)
        add(editCubePanel)
        exportPanel.position.y = 45f
        editCubePanel.position.y = 90f
        editCubePanel.hide()
    }

    class ProjectControlPanel : CPanel(width = 190f, height = 44f) {
        val newProjectButton = CButton("", 7f, 6f, 32f, 32f, "project.new")
        val loadProjectButton = CButton("", 43f, 6f, 32f, 32f, "project.load")
        val saveProjectButton = CButton("", 79f, 6f, 32f, 32f, "project.save")
        val saveAsProjectButton = CButton("", 115f, 6f, 32f, 32f, "project.save.as")
        val editProjectButton = CButton("", 151f, 6f, 32f, 32f, "project.edit")

        init {
            add(newProjectButton)
            add(loadProjectButton)
            add(saveProjectButton)
            add(saveAsProjectButton)
            add(editProjectButton)

            newProjectButton.setTooltip("New Project")
            loadProjectButton.setTooltip("Load Project")
            saveProjectButton.setTooltip("Save Project")
            saveAsProjectButton.setTooltip("Save Project As")
            editProjectButton.setTooltip("Edit Project")
        }

        override fun loadResources(resources: GuiResources) {
            newProjectButton.setImage(ImageIcon(resources.newProjectIcon).also { it.size = Vector2f(32f) })
            loadProjectButton.setImage(ImageIcon(resources.loadProjectCubeIcon).also { it.size = Vector2f(32f) })
            saveProjectButton.setImage(ImageIcon(resources.saveProjectIcon).also { it.size = Vector2f(32f) })
            saveAsProjectButton.setImage(ImageIcon(resources.saveAsProjectIcon).also { it.size = Vector2f(32f) })
            editProjectButton.setImage(ImageIcon(resources.editProjectIcon).also { it.size = Vector2f(32f) })
        }
    }

    class ExportPanel : CPanel(width = 190f, height = 44f) {
        val importModelButton = CButton("", 7f, 6f, 32f, 32f, "model.import")
        val exportModelButton = CButton("", 43f, 6f, 32f, 32f, "model.export")
        val exportTextureButton = CButton("", 79f, 6f, 32f, 32f, "texture.export")
        val hitboxMapButton = CButton("", 115f, 6f, 32f, 32f, "hitbox.export")
        val someButton = CButton("", 151f, 6f, 32f, 32f, "unassigned")

        init {
            add(importModelButton)
            add(exportModelButton)
            add(exportTextureButton)
            add(hitboxMapButton)
            add(someButton)

            importModelButton.setTooltip("Import Model")
            exportModelButton.setTooltip("Export Model")
            exportTextureButton.setTooltip("Export Texture Template")
            hitboxMapButton.setTooltip("Export Hitbox Map")
            someButton.setTooltip("Unassigned")
        }

        override fun loadResources(resources: GuiResources) {
            importModelButton.setImage(ImageIcon(resources.importModelIcon).also { it.size = Vector2f(32f) })
            exportModelButton.setImage(ImageIcon(resources.exportModelIcon).also { it.size = Vector2f(32f) })
            exportTextureButton.setImage(ImageIcon(resources.exportTextureIcon).also { it.size = Vector2f(32f) })
            hitboxMapButton.setImage(ImageIcon(resources.exportHitboxIcon).also { it.size = Vector2f(32f) })
//            someButton.setImage(ImageIcon(resources.editProjectIcon).also { it.size = Vector2f(32f) })
        }
    }
}