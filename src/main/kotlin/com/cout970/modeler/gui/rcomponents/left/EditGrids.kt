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
import com.cout970.reactive.nodes.*
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import org.joml.Vector2f
import org.liquidengine.legui.component.CheckBox
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.CharIcon
import org.liquidengine.legui.style.color.ColorConstants
import org.liquidengine.legui.style.font.FontRegistry

data class EditGridsProps(val gridLines: GridLines) : RProps

class EditGrids : RComponent<EditGridsProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditGrids") {
        style {
            transparent()
            border(2f) { greyColor }
            height = if (state.on) 345f else 24f
        }

        postMount {
            marginX(5f)
        }

        div("Title") {
            style {
                transparent()
                borderless()
                posY = 1f
                height = 24f
            }
            postMount {
                fillX()
            }

            comp(FixedLabel()) {
                style {
                    textState.text = "Config Grids"
                    fontSize = 22f
                    posX = 50f
                    sizeY = 22f
                }

                postMount {
                    sizeX = parent.sizeX - 100f
                }
            }

            // close button
            +IconButton(posX = 250f, posY = 3f).apply {
                val charCode = if (state.on) 'X' else 'O'
                setImage(CharIcon(Vector2f(16f, 16f), FontRegistry.DEFAULT, charCode, ColorConstants.lightGray()))
                background { darkColor }

                onRelease { setState { copy(on = !on) } }
            }
        }

        div("Offset") {
            style {
                transparent()
                borderless()
                posY = 30f
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
                posY = 145f
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

        div("Checkboxs") {
            style {
                transparent()
                borderless()
                posY = 110f + 110f + 10f + 25f
                height = 24f * 3 + 15f
            }

            postMount {
                marginX(10f)
            }

            +CheckBox("Enable Plane X", 0f, 0f, 278f - 10f, 24f).apply {
                defaultTextColor()
                style.setBorderRadius(0f)
                fontSize = 20f
                textState.padding.x = 24f
                isChecked = props.gridLines.enableXPlane
                background { darkColor }

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableXPlane = isChecked; rerender() }
            }

            +CheckBox("Enable Plane Y", 0f, 24f + 5f, 278f - 10f, 24f).apply {
                defaultTextColor()
                fontSize = 20f
                textState.padding.x = 24f
                isChecked = props.gridLines.enableYPlane
                background { darkColor }

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableYPlane = isChecked; rerender() }
            }

            +CheckBox("Enable Plane Z", 0f, 48f + 10f, 278f - 10f, 24f).apply {
                defaultTextColor()
                fontSize = 20f
                textState.padding.x = 24f
                isChecked = props.gridLines.enableZPlane
                background { darkColor }

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableZPlane = isChecked; rerender() }
            }
        }
    }

    private fun configIcon(icon: CharIcon) {
        icon.color = Config.colorPalette.whiteColor.toColor()
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