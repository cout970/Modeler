package com.cout970.modeler.gui.components

import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.gui.leguicomp.Panel
import com.cout970.modeler.render.tool.Animator
import org.joml.Vector2f
import org.liquidengine.legui.style.color.ColorConstants.*
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.renderer.nvg.NvgComponentRenderer
import org.liquidengine.legui.system.renderer.nvg.util.NvgShapes

class AnimationPanel(val animator: Animator, val animation: IAnimation) : Panel() {

    object Renderer : NvgComponentRenderer<AnimationPanel>() {

        override fun renderComponent(component: AnimationPanel, context: Context, nanovg: Long) {
            val size = component.animator.animationSize
            val time = component.animator.animationTime
            val animation = component.animation

            val pointerPos = component.size.x * time / size
            val absPos = component.absolutePosition

            val absPointerPos = Vector2f(absPos.x + pointerPos, absPos.y)

            // Background
            NvgShapes.drawRect(nanovg, component.absolutePosition, component.size, component.style.background.color,
                    component.style.cornerRadius)

            val scale = (component.size.x / size).toInt()

            for (i in 0..size.toInt()) {

                NvgShapes.drawRect(nanovg,
                        Vector2f(absPos.x + scale * i, absPos.y),
                        Vector2f(2f, component.size.y),
                        black(), 0f)
            }

            animation.operations.values.forEachIndexed { index, op ->

                val start = op.startTime * component.size.x / size
                val end = op.endTime * component.size.x / size

                NvgShapes.drawRect(nanovg,
                        Vector2f(absPos.x + start, absPos.y + index * 24f),
                        Vector2f(end - start, 24f),
                        lightGray(), 0f)
            }

            // Pointer
            NvgShapes.drawRect(nanovg, absPointerPos, Vector2f(2f, component.size.y), lightBlue(), 0f)
        }
    }
}