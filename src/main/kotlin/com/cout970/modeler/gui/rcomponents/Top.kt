package com.cout970.modeler.gui.rcomponents

import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.util.IPropertyBind
import com.cout970.modeler.util.PropertyManager
import com.cout970.reactive.core.*
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.style.border.SimpleLineBorder


class TopBar : RStatelessComponent<ModelAccessorProps>() {

    override fun RBuilder.render() = div("TopBar") {
        style {
            style.border = PixelBorder().also { it.enableBottom = true }
            background { darkestColor }
            height = 48f
        }

        postMount {
            fillX()
        }

        div("ProjectControls") {
            style {
                transparent()
                borderless()
                width = 240f
                height = 48f
            }

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

        div("ExportControls") {
            style {
                transparent()
                borderless()
                width = 192f
                height = 48f
                posX = 240f
            }

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

        child(SelectionTypeBar::class)
        child(ModelStatistics::class, ModelAccessorProps(props.access))
    }
}

data class SelectionTypeState(val type: SelectionType) : RState

class SelectionTypeBar : RComponent<EmptyProps, SelectionTypeState>() {

    override fun getInitialState() = SelectionTypeState(SelectionType.OBJECT)

    override fun RBuilder.render() = div("SelectionTypeBar") {
        style {
            background { blackColor }
            borderless()
            borderRadius(5f)
            posX = 432f + 10f
            posY = 5f
            width = 143f + 8f
            height = 40f
        }

        val firstButton = state.type == SelectionType.OBJECT
        val secondButton = state.type == SelectionType.FACE
        val thirdButton = state.type == SelectionType.EDGE
        val fourthButton = state.type == SelectionType.VERTEX

        +ToggleButton("", "selection_mode_object", firstButton, 4f, 4f, 32f, 32f).apply {
            tooltip = InstantTooltip("Selection mode: OBJECT")
            borderless()
            onClick { set(SelectionType.OBJECT) }
        }

        +ToggleButton("", "selection_mode_face", secondButton, 32f + 5f + 4f, 4f, 32f, 32f).apply {
            tooltip = InstantTooltip("Selection mode: FACE")
            borderless()
            onClick { set(SelectionType.FACE) }
        }

        +ToggleButton("", "selection_mode_edge", thirdButton, 64f + 10f + 4f, 4f, 32f, 32f).apply {
            tooltip = InstantTooltip("Selection mode: EDGE")
            borderless()
            onClick { set(SelectionType.EDGE) }
        }

        +ToggleButton("", "selection_mode_vertex", fourthButton, 96f + 15f + 4f, 4f, 32f, 32f).apply {
            tooltip = InstantTooltip("Selection mode: VERTEX")
            borderless()
            onClick { set(SelectionType.VERTEX) }
        }

        onCmd("updateSelectionType") { args ->
            setState { SelectionTypeState(args.getValue("value") as SelectionType) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun set(newType: SelectionType) {
        PropertyManager.findProperty("SelectionType")
                ?.let { it as IPropertyBind<SelectionType> }
                ?.set(newType)
    }
}


class ModelStatistics : RStatelessComponent<ModelAccessorProps>() {

    override fun RBuilder.render() = div("ModelStatistics") {
        style {
            background { darkestColor }
            style.border = PixelBorder().apply { enableLeft = true }
            rectCorners()
            width = 288f
            height = 85f
        }

        postMount {
            posX = parent.sizeX - sizeX
        }

        val model = props.access.model
        val objs = model.objects.size
        val quads = model.objects.map { it.mesh.faces.size }.sum()
        val posVertex = model.objects.map { it.mesh.pos.size }.sum()
        val texVertex = model.objects.map { it.mesh.tex.size }.sum()

        val config: FixedLabel.() -> Unit = {
            style.border = SimpleLineBorder()
            borderColor { color { lightDarkColor } }
            borderSize = 1f
            rectCorners()
            fontSize = 18f
            horizontalAlign = HorizontalAlign.LEFT
            textState.padding.x = 10f
        }

        +FixedLabel("Objs: $objs", 5f, 7f, 135f, 16f).apply(config)
        +FixedLabel("Quads: $quads", 5f, 27f, 135f, 16f).apply(config)
        +FixedLabel("Pos vertex: $posVertex", 144f, 7f, 140f, 16f).apply(config)
        +FixedLabel("Tex vertex: $texVertex", 144f, 27f, 140f, 16f).apply(config)

        on<EventModelUpdate> {
            rerender()
        }
    }
}