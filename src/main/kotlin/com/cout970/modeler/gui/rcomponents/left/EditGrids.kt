package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.canvas.GridLines
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.rcomponents.FloatInput
import com.cout970.modeler.gui.rcomponents.FloatInputProps
import com.cout970.modeler.util.toColor
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.DivBuilder
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import org.joml.Vector2f
import org.liquidengine.legui.component.CheckBox
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.CharIcon

data class EditGridsProps(val gridLines: GridLines) : RProps

class EditGrids : RComponent<EditGridsProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditGrids") {
        style {
            classes("left_panel_group", "config_grids")
            height = if (state.on) 355f else 24f
        }

        postMount {
            marginX(5f)
            alignAsColumn(5f, 20f)
        }

        child(GroupTitle::class.java, GroupTitleProps("Config Grids", state.on) { setState { copy(on = !on) } })

        div("Checkboxes") {
            style {
                classes("config_grids_checkboxes")
                height = 24f * 3 + 15f
            }

            postMount {
                marginX(10f)
            }

            +CheckBox("Enable Plane X", 0f, 0f, 248f, 24f).apply {
                defaultTextColor()
                style.setBorderRadius(0f)
                fontSize = 20f
                textState.padding.x = 24f
                isChecked = props.gridLines.enableXPlane
                background { dark2 }
                classes("checkbox")
                if (isChecked) classes("checkbox_active")

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableXPlane = isChecked; rerender() }
            }

            +CheckBox("Enable Plane Y", 0f, 24f + 5f, 248f, 24f).apply {
                defaultTextColor()
                fontSize = 20f
                textState.padding.x = 24f
                isChecked = props.gridLines.enableYPlane
                background { dark2 }
                classes("checkbox")
                if (isChecked) classes("checkbox_active")

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableYPlane = isChecked; rerender() }
            }

            +CheckBox("Enable Plane Z", 0f, 48f + 10f, 248f, 24f).apply {
                defaultTextColor()
                fontSize = 20f
                textState.padding.x = 24f
                isChecked = props.gridLines.enableZPlane
                background { dark2 }
                classes("checkbox")
                if (isChecked) classes("checkbox_active")

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableZPlane = isChecked; rerender() }
            }
        }

        div("Offset") {
            style {
                transparent()
                borderless()
                height = 110f
            }

            postMount {
                fillX()
            }

            val gridOffsetX = props.gridLines.gridOffset::xf.getter
            val gridOffsetY = props.gridLines.gridOffset::yf.getter
            val gridOffsetZ = props.gridLines.gridOffset::zf.getter

            +FixedLabel("Grid offset", 0f, 0f, 278f, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput(gridOffsetX, "offset", "x", vec2Of(10f, 20f))
            valueInput(gridOffsetY, "offset", "y", vec2Of(98f, 20f))
            valueInput(gridOffsetZ, "offset", "z", vec2Of(185f, 20f))
        }

        div("Size") {
            style {
                transparent()
                borderless()
                height = 110f
            }

            postMount {
                fillX()
            }

            val gridSizeX = props.gridLines.gridSize::xf.getter
            val gridSizeY = props.gridLines.gridSize::yf.getter
            val gridSizeZ = props.gridLines.gridSize::zf.getter

            +FixedLabel("Grid size", 0f, 0f, 278f, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput(gridSizeX, "size", "x", vec2Of(10f, 20f))
            valueInput(gridSizeY, "size", "y", vec2Of(98f, 20f))
            valueInput(gridSizeZ, "size", "z", vec2Of(185f, 20f))
        }
    }

    private fun configIcon(icon: CharIcon) {
        icon.color = Config.colorPalette.bright4.toColor()
        icon.position = Vector2f(4f, 4f)
        icon.horizontalAlign = HorizontalAlign.CENTER
    }

    fun DivBuilder.valueInput(getter: () -> Float, target: String, axis: String, pos: IVector2) {
        child(FloatInput::class, FloatInputProps(
                getter = getter,
                command = "grid.$target.change",
                metadata = mapOf("axis" to axis, "listener" to { rerender() }),
                enabled = true,
                pos = pos
        ))
    }
}