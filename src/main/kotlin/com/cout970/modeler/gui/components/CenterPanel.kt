package com.cout970.modeler.gui.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.leguicomp.FixedLabel
import com.cout970.modeler.gui.leguicomp.Panel
import com.cout970.modeler.gui.leguicomp.ProfilerDiagram
import com.cout970.modeler.gui.leguicomp.panel
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.gui.views.VisibleElements
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent
import com.cout970.modeler.util.toColor
import org.joml.Vector2f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign

/**
 * Created by cout970 on 2017/09/28.
 */
class CenterPanel : RComponent<CenterPanel.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuilder): Component = panel root@ {
        val left = if (props.visibleElements.left) 280f else 0f
        val right = if (props.visibleElements.right) 190f else 0f
        width = ctx.parentSize.xf - left - right
        height = ctx.parentSize.yf - 48f - 200f
        posX = left
        posY = 48f
        setTransparent()
        setBorderless()

        val canvas = Panel().apply {
            position = Vector2f()
            size = this@root.size
            setBorderless()
            setTransparent()
            if (props.canvasContainer.canvas.isEmpty()) {
                hide()
            }
        }

        props.canvasContainer.panel = canvas
        props.canvasContainer.refreshCanvas()
        props.canvasContainer.layout.updateCanvas()

        // add canvas to root
        +canvas

        +panel {
            if (canvas.isEnabled) {
                hide()
            }

            size = this@root.size
            backgroundColor = Config.colorPalette.blackColor.toColor()
            setBorderless()

            val x = size.x / 2 - 125f
            val y = size.y / 2 - 50f

            // first level
            +FixedLabel("Open new view:", x, y + 0f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Close view:", x, y + 25f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Resize view:", x, y + 50f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Change mode:", x, y + 75f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Hide left:", x, y + 100f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Hide right:", x, y + 125f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            // second level

            +FixedLabel("Alt + N", 150f + x, y + 0f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Alt + D", 150f + x, y + 25f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Alt + J/K", 150f + x, y + 50f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Alt + M", 150f + x, y + 75f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Alt + L", 150f + x, y + 100f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Alt + R", 150f + x, y + 125f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

        }

        +ProfilerDiagram()
    }

    class Props(val visibleElements: VisibleElements, val canvasContainer: CanvasContainer)

    companion object : RComponentSpec<CenterPanel, CenterPanel.Props, Unit>
}