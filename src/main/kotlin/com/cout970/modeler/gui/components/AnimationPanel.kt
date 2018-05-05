package com.cout970.modeler.gui.components

import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.gui.leguicomp.Panel
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJoml4f
import com.cout970.reactive.dsl.posX
import com.cout970.reactive.dsl.sizeX
import com.cout970.vector.extensions.times
import com.cout970.vector.extensions.vec4Of
import org.joml.Vector2f
import org.joml.Vector4f
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.component.optional.align.VerticalAlign
import org.liquidengine.legui.style.color.ColorConstants
import org.liquidengine.legui.style.color.ColorConstants.*
import org.liquidengine.legui.style.font.FontRegistry
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.renderer.nvg.NvgComponentRenderer
import org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.createScissor
import org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.resetScissor
import org.liquidengine.legui.system.renderer.nvg.util.NvgShapes
import org.liquidengine.legui.system.renderer.nvg.util.NvgText
import kotlin.math.max
import kotlin.math.roundToInt

class AnimationPanel(val animator: Animator, val animation: IAnimation) : Panel() {

    object Renderer : NvgComponentRenderer<AnimationPanel>() {

        override fun renderComponent(comp: AnimationPanel, context: Context, nanovg: Long) {
            createScissor(nanovg, comp)
            val style = comp.style
            NvgShapes.drawRect(nanovg, comp.absolutePosition, comp.size, style.background.color, style.borderRadius)
            comp.render(context, nanovg)
            resetScissor(nanovg)
        }


        fun AnimationPanel.render(context: Context, nanovg: Long) {
            val absPos = absolutePosition

            // channels background
            val color = (style.background.color.toIVector() * vec4Of(1.1, 1.1, 1.1, 1.0)).toJoml4f()
            repeat(animation.channels.size) { index ->

                NvgShapes.drawRect(nanovg,
                        Vector2f(absPos.x, absPos.y + index * 24f),
                        Vector2f(size.x, 24f),
                        color, 0f)
            }

            // time lines
            val time = animator.animationTime
            val zoom = animator.zoom

            val pixelOffset = 0
            val timeToPixel = size.x / zoom
            val frameSize = 1.0 / 60.0 * timeToPixel
            val markSize = frameSize.toFloat() * 10f
            val marks = Math.ceil(size.x / markSize.toDouble())

            val filterOdds = if (zoom < 2f) 1 else {
                val log2 = Math.log(zoom.toDouble()) / Math.log(2.0)
                max(1, 1 shl Math.floor(log2 - 1f).toInt())
            }

            repeat(marks.toInt()) {

                if (it % filterOdds != 0) return@repeat

                NvgShapes.drawRect(nanovg,
                        Vector2f(absPos.x + markSize * it + pixelOffset, absPos.y),
                        Vector2f(2f, size.y),
                        black(), 0f)
            }

            // Pointer

            val pointerPos = timeToPixel * time + pixelOffset
            val absPointerPos = Vector2f(absPos.x + pointerPos, absPos.y)

            NvgShapes.drawRect(nanovg, absPointerPos, Vector2f(2f, size.y), lightBlue(), 0f)
        }
    }
}

class AnimationPanelHead(val animator: Animator, val animation: IAnimation) : Panel() {

    object Renderer : NvgComponentRenderer<AnimationPanelHead>() {

        override fun renderComponent(comp: AnimationPanelHead, context: Context, nanovg: Long) {
            createScissor(nanovg, comp)
            val style = comp.style
            NvgShapes.drawRect(nanovg, comp.absolutePosition, comp.size, style.background.color, style.borderRadius)
            comp.render(context, nanovg)
            resetScissor(nanovg)
        }

        fun AnimationPanelHead.render(context: Context, nanovg: Long) {
            val absPos = absolutePosition
            val zoom = animator.zoom
            val width = parent.sizeX - 16f - 200f

            val pixelOffset = 0
            val timeToPixel = width / zoom
            val frameSize = 1.0 / 60.0 * timeToPixel
            val markSize = frameSize.toFloat() * 10f
            val marks = Math.ceil(width / markSize.toDouble())

            val filterOdds = if (zoom < 2f) 1 else {
                val log2 = Math.log(zoom.toDouble()) / Math.log(2.0)
                max(1, 1 shl Math.floor(log2 - 1f).toInt())
            }

            repeat(marks.toInt()) {

                if (it % filterOdds != 0) return@repeat

                val frame = (it * markSize / frameSize).roundToInt()

                NvgText.drawTextLineToRect(
                        nanovg,
                        Vector4f(absPos.x + markSize * it + pixelOffset - 30f, absPos.y, 60f, 20f),
                        false,
                        HorizontalAlign.CENTER,
                        VerticalAlign.MIDDLE,
                        16f,
                        FontRegistry.DEFAULT,
                        frame.toString(),
                        ColorConstants.white()
                )
            }
        }
    }
}