package com.cout970.modeler.gui.rcomponents

import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.util.IPropertyBind
import com.cout970.modeler.util.PropertyManager
import com.cout970.reactive.core.*
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style


class TopBar : RStatelessComponent<EmptyProps>() {

    override fun RBuilder.render() = div("TopBar") {
        style {
            borderless()
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
    }
}

data class SelectionTypeState(val type: SelectionType) : RState

class SelectionTypeBar : RComponent<EmptyProps, SelectionTypeState>() {

    override fun getInitialState() = SelectionTypeState(SelectionType.OBJECT)

    override fun RBuilder.render() = div("SelectionTypeBar") {
        style {
            background { blackColor }
            borderless()
            cornerRadius(5f)
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