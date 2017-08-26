package com.cout970.modeler.gui.editor.toppanel

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.GuiResources
import com.cout970.modeler.gui.comp.CButton
import com.cout970.modeler.gui.comp.CPanel
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.util.toColor
import org.joml.Vector2f
import org.liquidengine.legui.icon.ImageIcon

/**
 * Created by cout970 on 2017/07/30.
 */
class TopPanel : CPanel(0f, 36f) {

    val projectControlPanel = ProjectControlPanel()
    val exportPanel = ExportPanel()

    init {
        add(projectControlPanel)
        add(exportPanel)
        backgroundColor = Config.colorPalette.topPanelColor.toColor()
        exportPanel.position.x = 180f
        setBorderless()
    }

    class ProjectControlPanel : CPanel(width = 184f, height = 36f) {
        val newProjectButton = CButton("", 2f, 2f, 32f, 32f, "project.new")
        val loadProjectButton = CButton("", 38f, 2f, 32f, 32f, "project.load")
        val saveProjectButton = CButton("", 74f, 2f, 32f, 32f, "project.save")
        val saveAsProjectButton = CButton("", 110f, 2f, 32f, 32f, "project.save.as")
        val editProjectButton = CButton("", 146f, 2f, 32f, 32f, "project.edit")

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
            setBorderless()
            setTransparent()
        }

        override fun loadResources(resources: GuiResources) {
            newProjectButton.setImage(ImageIcon(resources.newProjectIcon).also { it.size = Vector2f(32f) })
            loadProjectButton.setImage(ImageIcon(resources.loadProjectCubeIcon).also { it.size = Vector2f(32f) })
            saveProjectButton.setImage(ImageIcon(resources.saveProjectIcon).also { it.size = Vector2f(32f) })
            saveAsProjectButton.setImage(ImageIcon(resources.saveAsProjectIcon).also { it.size = Vector2f(32f) })
            editProjectButton.setImage(ImageIcon(resources.editProjectIcon).also { it.size = Vector2f(32f) })
        }
    }

    class ExportPanel : CPanel(width = 190f, height = 36f) {
        val importModelButton = CButton("", 2f, 2f, 32f, 32f, "model.import")
        val exportModelButton = CButton("", 38f, 2f, 32f, 32f, "model.export")
        val exportTextureButton = CButton("", 74f, 2f, 32f, 32f, "texture.export")
        val hitboxMapButton = CButton("", 110f, 2f, 32f, 32f, "model.export.hitboxes")

        init {
            add(importModelButton)
            add(exportModelButton)
            add(exportTextureButton)
            add(hitboxMapButton)

            importModelButton.setTooltip("Import Model")
            exportModelButton.setTooltip("Export Model")
            exportTextureButton.setTooltip("Export Texture Template")
            hitboxMapButton.setTooltip("Export Hitbox Map")
            setBorderless()
            setTransparent()
        }

        override fun loadResources(resources: GuiResources) {
            importModelButton.setImage(ImageIcon(resources.importModelIcon).also { it.size = Vector2f(32f) })
            exportModelButton.setImage(ImageIcon(resources.exportModelIcon).also { it.size = Vector2f(32f) })
            exportTextureButton.setImage(ImageIcon(resources.exportTextureIcon).also { it.size = Vector2f(32f) })
            hitboxMapButton.setImage(ImageIcon(resources.exportHitboxIcon).also { it.size = Vector2f(32f) })
        }
    }
}