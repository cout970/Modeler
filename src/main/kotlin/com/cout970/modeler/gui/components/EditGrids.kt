package com.cout970.modeler.gui.components

import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.canvas.GridLines
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.gui.reactive.invoke
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent
import com.cout970.modeler.util.toColor
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import org.joml.Vector2f
import org.liquidengine.legui.component.CheckBox
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.CharIcon

class EditGrids : RComponent<EditGrids.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuilder): Component = panel root@ {
        setTransparent()
        marginX(ctx, 5f)
        border(3f) { greyColor }
        posY = 132f + 444f
        height = 320f

        +panel {
            posY = 5f
            width = this@root.width
            height = 110f
            setTransparent()
            setBorderless()

            val gridOffsetX = props.gridLines.gridOffset::xf.getter
            val gridOffsetY = props.gridLines.gridOffset::yf.getter
            val gridOffsetZ = props.gridLines.gridOffset::zf.getter

            +FixedLabel("Grid offset", 0f, 0f, width, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput(gridOffsetX, "offset", "x", vec2Of(10f, 20f))
            valueInput(gridOffsetY, "offset", "y", vec2Of(98f, 20f))
            valueInput(gridOffsetZ, "offset", "z", vec2Of(185f, 20f))
        }

        +panel {
            width = this@root.width
            posY = 110f + 10f
            height = 110f
            setTransparent()
            setBorderless()

            val gridSizeX = props.gridLines.gridSize::xf.getter
            val gridSizeY = props.gridLines.gridSize::yf.getter
            val gridSizeZ = props.gridLines.gridSize::zf.getter

            +FixedLabel("Grid size", 0f, 0f, width, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput(gridSizeX, "size", "x", vec2Of(10f, 20f))
            valueInput(gridSizeY, "size", "y", vec2Of(98f, 20f))
            valueInput(gridSizeZ, "size", "z", vec2Of(185f, 20f))
        }

        +panel {
            marginX(ctx, 10f)
            posY = 110f + 110f + 10f
            height = 24f * 3 + 15f
            setTransparent()
            setBorderless()

            +CheckBox("Enable Plane X", 0f, 0f, width - 10f, 24f).apply {
                defaultTextColor()
                cornerRadius = 0f
                fontSize(20f)
                textState.padding.x = 24f
                isChecked = props.gridLines.enableXPlane
                background { darkColor }

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableXPlane = isChecked; rebuild() }
            }

            +CheckBox("Enable Plane Y", 0f, 24f + 5f, width - 10f, 24f).apply {
                defaultTextColor()
                fontSize(20f)
                textState.padding.x = 24f
                isChecked = props.gridLines.enableYPlane
                background { darkColor }

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableYPlane = isChecked; rebuild() }
            }

            +CheckBox("Enable Plane Z", 0f, 48f + 10f, width - 10f, 24f).apply {
                defaultTextColor()
                fontSize(20f)
                textState.padding.x = 24f
                isChecked = props.gridLines.enableZPlane
                background { darkColor }

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableZPlane = isChecked; rebuild() }
            }
        }
    }

    private fun configIcon(icon: CharIcon) {
        icon.color = Config.colorPalette.whiteColor.toColor()
        icon.position = Vector2f(4f, 4f)
        icon.horizontalAlign = HorizontalAlign.CENTER
    }

    fun Panel.valueInput(getter: () -> Float, target: String, axis: String, pos: IVector2) {
        val properties = mapOf("axis" to axis, "listener" to { replaceState(Unit) })
        +ValueInput {
            ValueInput.Props(
                    dispatcher = props.dispatcher,
                    value = getter,
                    cmd = "grid.$target.change",
                    metadata = properties,
                    enabled = true,
                    pos = pos
            )
        }
    }

    class Props(val dispatcher: Dispatcher, val gridLines: GridLines)

    companion object : RComponentSpec<EditGrids, Props, Unit>
}