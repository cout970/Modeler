package com.cout970.modeler.gui.react.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponent
import com.cout970.modeler.gui.react.core.RComponentSpec
import com.cout970.modeler.gui.react.leguicomp.IconButton
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.gui.react.scalable.FixedYFillX
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/07.
 */
class TopButtonPanel : RComponent<Unit, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuildContext): Component = panel {
        backgroundColor = Config.colorPalette.darkestColor.toColor()
        FixedYFillX(48f).updateScale(this, ctx.parentSize)
        setBorderless()

        panel {
            setTransparent()
            width = 240f
            height = 48f

            +IconButton("project.new", "newProjectIcon", 0f, 0f, 48f, 48f).also {
                it.setTooltip("New Project")
            }
            +IconButton("project.load", "loadProjectCubeIcon", 48f, 0f, 48f, 48f).also {
                it.setTooltip("Load Project")
            }
            +IconButton("project.save", "saveProjectIcon", 96f, 0f, 48f, 48f).also {
                it.setTooltip("Save Project")
            }
            +IconButton("project.save.as", "saveAsProjectIcon", 144f, 0f, 48f, 48f).also {
                it.setTooltip("Save Project As")
            }
            +IconButton("project.edit", "editProjectIcon", 192f, 0f, 48f, 48f).also {
                it.setTooltip("Edit Project")
            }
        }
        panel {
            setTransparent()
            width = 192f
            height = 48f
            position.x = 240f

            +IconButton("model.import", "importModelIcon", 0f, 0f, 48f, 48f).also {
                it.setTooltip("Import Model")
            }
            +IconButton("model.export", "exportModelIcon", 48f, 0f, 48f, 48f).also {
                it.setTooltip("Export Model")
            }
            +IconButton("texture.export", "exportTextureIcon", 96f, 0f, 48f, 48f).also {
                it.setTooltip("Export Texture Template")
            }
            +IconButton("model.export.hitboxes", "exportHitboxIcon", 144f, 0f, 48f, 48f).also {
                it.setTooltip("Export Hitbox Map")
            }
        }
    }

    companion object : RComponentSpec<TopButtonPanel, Unit, Unit>
}