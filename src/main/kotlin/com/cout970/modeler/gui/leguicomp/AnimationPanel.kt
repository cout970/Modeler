package com.cout970.modeler.gui.leguicomp

import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.core.animation.ref
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJoml4f
import com.cout970.reactive.dsl.sizeX
import com.cout970.vector.extensions.times
import com.cout970.vector.extensions.vec4Of
import org.joml.Vector2f
import org.joml.Vector4f
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.component.optional.align.VerticalAlign
import org.liquidengine.legui.style.color.ColorConstants.*
import org.liquidengine.legui.style.font.FontRegistry
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.renderer.nvg.NvgComponentRenderer
import org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.createScissorByParent
import org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.resetScissor
import org.liquidengine.legui.system.renderer.nvg.util.NvgShapes
import org.liquidengine.legui.system.renderer.nvg.util.NvgText
import kotlin.math.*

private fun baseForZoom(zoom: Float): Int {
    return if (zoom < 2f) 1 else {
        val log2 = log(zoom.toDouble(), 2.0)
        max(1, 1 shl floor(log2 - 1f).toInt())
    }
}

private const val FRAMES_BETWEEN_MARKS = 5f

class AnimationPanel(val animator: Animator, val animation: IAnimation) : Panel() {

    object Renderer : NvgComponentRenderer<AnimationPanel>() {

        override fun renderComponent(comp: AnimationPanel, context: Context, nanovg: Long) {
            createScissorByParent(nanovg, comp)
            val style = comp.style
            NvgShapes.drawRect(nanovg, comp.absolutePosition, comp.size, style.background.color, style.borderRadius)
            comp.render(nanovg)
            resetScissor(nanovg)
        }


        fun AnimationPanel.render(nanovg: Long) {
            val absPos = absolutePosition
            val time = animator.animationTime
            val zoom = animator.zoom

            val timeToPixel = size.x / zoom
            val pixelOffset = animator.offset * timeToPixel
            val frameSize = 1.0 / 60.0 * timeToPixel

            // channels background
            val color = (style.background.color.toIVector() * vec4Of(1.3, 1.3, 1.3, 1.0)).toJoml4f()
            val channelLength = animation.timeLength * timeToPixel

            // Channel background
            repeat(animation.channels.size) { index ->
                NvgShapes.drawRect(nanovg,
                    Vector2f(absPos.x + pixelOffset, absPos.y + index * 24f),
                    Vector2f(channelLength, 24f),
                    color, 0f)
            }

            // Channel separation line
            repeat(animation.channels.size) { index ->
                NvgShapes.drawRect(nanovg,
                    Vector2f(absPos.x, 24f + absPos.y + index * 24f),
                    Vector2f(size.x, 1f),
                    black(), 0f)
            }


            // time lines
            val start = -animator.offset * 60f
            val markSize = frameSize.toFloat() * FRAMES_BETWEEN_MARKS
            val filterOdds = baseForZoom(zoom * 10f / FRAMES_BETWEEN_MARKS)
            var barIndex = ceil(start / FRAMES_BETWEEN_MARKS).toInt()
            var current = (barIndex * FRAMES_BETWEEN_MARKS / 60f) * timeToPixel + pixelOffset

            while (current < size.x) {
                if (barIndex % filterOdds == 0) {
                    NvgShapes.drawRect(nanovg,
                        Vector2f(absPos.x + current, absPos.y),
                        Vector2f(2f, size.y),
                        black(), 0f)
                }

                current += markSize
                barIndex++
            }

            // keyframes
            animation.channels.values.forEachIndexed { index, c ->
                val selected = animator.selectedChannel == c.ref

                c.keyframes.forEachIndexed { i, keyframe ->

                    val pos = keyframe.time * timeToPixel + pixelOffset
                    val innerColor = if (selected && animator.selectedKeyframe == i) lightGreen() else lightRed()

                    NvgShapes.drawRect(nanovg,
                        Vector2f(absPos.x + pos - 12f + 2f, absPos.y + 2f + index * 24f),
                        Vector2f(20f, 20f),
                        lightBlack(), 0f)

                    NvgShapes.drawRect(nanovg,
                        Vector2f(absPos.x + pos - 7f, absPos.y + 5f + index * 24f),
                        Vector2f(14f, 14f),
                        innerColor, 0f)
                }
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
            createScissorByParent(nanovg, comp)
            val style = comp.style
            NvgShapes.drawRect(nanovg, comp.absolutePosition, comp.size, style.background.color, style.borderRadius)
            comp.render(nanovg)
            resetScissor(nanovg)
        }

        fun AnimationPanelHead.render(nanovg: Long) {
            val absPos = absolutePosition
            val zoom = animator.zoom
            val width = parent.sizeX - 16f - 200f

            val timeToPixel = width / zoom
            val pixelOffset = animator.offset * timeToPixel
            val frameSize = 1.0 / 60.0 * timeToPixel

            // time lines
            val start = -animator.offset * 60f
            val markSize = frameSize.toFloat() * FRAMES_BETWEEN_MARKS
            val filterOdds = baseForZoom(zoom * 10f / FRAMES_BETWEEN_MARKS)
            var barIndex = ceil(start / FRAMES_BETWEEN_MARKS).toInt()
            var current = (barIndex * FRAMES_BETWEEN_MARKS / 60f) * timeToPixel + pixelOffset

            while (current < size.x) {
                if (barIndex % filterOdds == 0) {

                    val frame = (barIndex * markSize / frameSize).roundToInt()
                    val offsetX = markSize * barIndex + pixelOffset

                    if (offsetX >= 0) {
                        renderMark(nanovg, absPos.x + offsetX, absPos.y, frame)
                    }
                }

                current += markSize
                barIndex++
            }
        }

        fun renderMark(nanovg: Long, posX: Float, posY: Float, frame: Int) {
            NvgText.drawTextLineToRect(
                nanovg,
                Vector4f(posX - 30f, posY, 60f, 20f),
                false,
                HorizontalAlign.CENTER,
                VerticalAlign.MIDDLE,
                18f,
                FontRegistry.DEFAULT,
                frame.toString(),
                white()
            )
        }
    }
}