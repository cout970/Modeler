package com.cout970.modeler.gui.rcomponents.right

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.leguicomp.FixedLabel
import com.cout970.modeler.gui.leguicomp.IconButton
import com.cout970.modeler.gui.leguicomp.border
import com.cout970.modeler.util.toColor
import com.cout970.reactive.core.EmptyProps
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.optional.align.HorizontalAlign

class CreateObjectPanel : RStatelessComponent<EmptyProps>() {

    override fun RBuilder.render() = div("CreateObject") {
        style {
            transparent()
            border(2f) { grey }
            rectCorners()
        }

        postMount {
            marginX(5f)
            height = 64f
        }

        comp(FixedLabel()) {
            style {
                textState.apply {
                    this.text = "Create Object"
                    textColor = Config.colorPalette.textColor.toColor()
                    horizontalAlign = HorizontalAlign.CENTER
                    fontSize = 20f
                }

            }

            postMount {
                marginX(50f)
                posY = 0f
                sizeY = 24f
            }
        }

        +IconButton("cube.template.new", "addTemplateCubeIcon", 5f, 28f, 32f, 32f).also {
            it.setTooltip("Create Template Cube")
        }
        +IconButton("cube.mesh.new", "addMeshCubeIcon", 40f, 28f, 32f, 32f).also {
            it.setTooltip("Create Cube Mesh")
        }
        +IconButton("group.add", "addMeshCubeIcon", 75f, 28f, 32f, 32f).also {
            it.setTooltip("Create Object GroupRef")
        }
    }
}