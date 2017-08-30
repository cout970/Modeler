package com.cout970.modeler.gui.editor.toppanel

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.GuiResources
import com.cout970.modeler.gui.comp.*
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.icon.ImageIcon

/**
 * Created by cout970 on 2017/07/30.
 */
class TopPanel : CPanel(0f, 48f) {

    val projectControlPanel = ProjectControlPanel()
    val exportPanel = ExportPanel()

    init {
        add(projectControlPanel)
        add(exportPanel)
        backgroundColor = Config.colorPalette.topPanelColor.toColor()
        exportPanel.position.x = 240f
        setBorderless()
        (border as PixelBorder).also { it.isEnabled = true }.enableBottom = true
    }

    class ProjectControlPanel : CPanel(width = 240f, height = 48f) {
        val newProjectButton = CButton("", 0f, 0f, 48f, 48f, "project.new")
        val loadProjectButton = CButton("", 48f, 0f, 48f, 48f, "project.load")
        val saveProjectButton = CButton("", 96f, 0f, 48f, 48f, "project.save")
        val saveAsProjectButton = CButton("", 144f, 0f, 48f, 48f, "project.save.as")
        val editProjectButton = CButton("", 192f, 0f, 48f, 48f, "project.edit")

        init {
            add(newProjectButton.apply { setTransparent() })
            add(loadProjectButton.apply { setTransparent() })
            add(saveProjectButton.apply { setTransparent() })
            add(saveAsProjectButton.apply { setTransparent() })
            add(editProjectButton.apply { setTransparent() })

            newProjectButton.setTooltip("New Project")
            loadProjectButton.setTooltip("Load Project")
            saveProjectButton.setTooltip("Save Project")
            saveAsProjectButton.setTooltip("Save Project As")
            editProjectButton.setTooltip("Edit Project")
            setBorderless()
            setTransparent()
        }

        override fun loadResources(resources: GuiResources) {
            newProjectButton.setImage(ImageIcon(resources.newProjectIcon))
            loadProjectButton.setImage(ImageIcon(resources.loadProjectCubeIcon))
            saveProjectButton.setImage(ImageIcon(resources.saveProjectIcon))
            saveAsProjectButton.setImage(ImageIcon(resources.saveAsProjectIcon))
            editProjectButton.setImage(ImageIcon(resources.editProjectIcon))
        }
    }

    class ExportPanel : CPanel(width = 192f, height = 48f) {
        val importModelButton = CButton("", 0f, 0f, 48f, 48f, "model.import")
        val exportModelButton = CButton("", 48f, 0f, 48f, 48f, "model.export")
        val exportTextureButton = CButton("", 96f, 0f, 48f, 48f, "texture.export")
        val hitboxMapButton = CButton("", 144f, 0f, 48f, 48f, "model.export.hitboxes")

        init {
            add(importModelButton.apply { setTransparent() })
            add(exportModelButton.apply { setTransparent() })
            add(exportTextureButton.apply { setTransparent() })
            add(hitboxMapButton.apply { setTransparent() })

            importModelButton.setTooltip("Import Model")
            exportModelButton.setTooltip("Export Model")
            exportTextureButton.setTooltip("Export Texture Template")
            hitboxMapButton.setTooltip("Export Hitbox Map")
            setBorderless()
            setTransparent()
        }

        override fun loadResources(resources: GuiResources) {
            importModelButton.setImage(ImageIcon(resources.importModelIcon))
            exportModelButton.setImage(ImageIcon(resources.exportModelIcon))
            exportTextureButton.setImage(ImageIcon(resources.exportTextureIcon))
            hitboxMapButton.setImage(ImageIcon(resources.exportHitboxIcon))
        }
    }
}