package com.cout970.modeler.gui.editor.bottompanel

import com.cout970.modeler.gui.comp.*
import com.cout970.modeler.util.BooleanPropertyWrapper
import com.cout970.modeler.util.hide
import org.joml.Vector2f
import org.liquidengine.legui.color.ColorConstants
import org.liquidengine.legui.component.ScrollBar
import org.liquidengine.legui.component.optional.Orientation
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.renderer.nvg.NvgComponentRenderer
import org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.drawRectangle

/**
 * Created by cout970 on 2017/08/26.
 */
class BottomPanel : CPanel() {

    val buttonPanel = ButtonPanel()
    val scrollBar = ScrollBar()
    val timelinePanel = TimelinePanel()

    init {
        add(buttonPanel)
        add(scrollBar)
        add(timelinePanel)
        setTransparent()
        setBorderless()
        scrollBar.orientation = Orientation.HORIZONTAL
        hide()
    }

    class ButtonPanel : CPanel() {

        var offset = 5f
        val startButton = CButton("", offset, 0f, 24f, 16f, "")
        val previousKeyButton = CButton("", offset + 26f, 0f, 24f, 16f, "")
        val playButton = CToggleButton(offset + 26f * 2, 0f, 24f, 16f, false) {
            BooleanPropertyWrapper(it::playAnimation)
        }
        val nextKeyButton = CButton("", offset + 26f * 3, 0f, 24f, 16f, "")
        val endButton = CButton("", offset + 26f * 4, 0f, 24f, 16f, "")

        val addKeyframeButton = CButton("", offset + 26f * 4, 0f, 24f, 16f, "")
        val removeKeyframeButton = CButton("", offset + 26f * 4, 0f, 24f, 16f, "")

        init {
            add(startButton)
            add(previousKeyButton)
            add(playButton)
            add(nextKeyButton)
            add(endButton)
        }
    }

    class TimelinePanel : CPanel() {

        var scale = 1f
        var offset = 0f

        init {
            setBorderless()
        }

        object TimelinePanelRenderer : NvgComponentRenderer<TimelinePanel>() {

            fun nonZeroOrElse(a: Float, other: Float): Float {
                if (a == 0f) return other
                return a
            }

            override fun renderComponent(comp: TimelinePanel, context: Context, nanovg: Long) {

                val interval = nonZeroOrElse(10 * comp.scale, 1f)
                val lines = (comp.size.x / interval).toInt()
                val offsetNorm = comp.offset % interval

                (0..lines)
                        .map { offsetNorm + it.toFloat() * interval }
                        .filter { it >= 0 }
                        .forEach {
                            drawRectangle(nanovg, ColorConstants.white(),
                                    Vector2f(it, 0f).add(comp.screenPosition),
                                    Vector2f(1f, comp.size.y))
                        }
            }
        }
    }
}