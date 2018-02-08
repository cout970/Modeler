package com.cout970.modeler.gui.components

import com.cout970.modeler.api.animation.AnimationState
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.event.EventAnimatorUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.gui.reactive.invoke
import com.cout970.modeler.gui.views.VisibleElements
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent
import com.cout970.vector.extensions.vec2Of
import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign

class BottomPanel : RComponent<BottomPanel.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuilder): Component = panel {
        val left = if (props.visibleElements.left) 288f else 0f
        val right = if (props.visibleElements.right) 190f else 0f
        val totalWidth = ctx.parentSize.xf - left - right
        val totalHeight = 200f
        width = totalWidth
        height = totalHeight
        posX = left
        posY = ctx.parentSize.yf - 200f

        background { darkColor }
        setBorderless()

        if (!props.visibleElements.bottom) hide()

        +panel {
            height = 32f
            width = totalWidth

            background { darkestColor }
            setBorderless()

            +controlPanel()
        }
        +operationList(totalWidth)
        +panel {
            width = totalWidth
            height = 24f
            posY = 32f
            background { darkColor }
            border { blackColor }
        }

        +AnimationPanel(props.animator, props.modelAccessor.animation).apply {
            posY = 32f + 24f
            posX = 100f
            width = totalWidth - posX
            height = totalHeight - posY
            background { darkColor }
        }

        listenerMap.addListener(EventAnimatorUpdate::class.java) { rebuild() }
    }

    private fun operationList(totalWidth: Float) = panel {
        posY = 32f + 24f
        height = 200f - 32f
        width = totalWidth
        setTransparent()
        var index = 0
        val operations = props.modelAccessor.animation.operations.values

        operations.forEach { op ->
            +panel {
                posY = index * 24f
                width = 100f
                height = 24f
                background { greyColor }

                border = PixelBorder().apply {
                    enableBottom = true
                    color = Vector4f(0f, 0f, 0f, 1f)
                }

                +FixedLabel(op.name, x = 5f, width = 100f).apply {
                    textState.horizontalAlign = HorizontalAlign.LEFT
                }
            }
            index++
        }
    }

    private fun controlPanel() = panel {
        width = 32f * 6f + 120f
        height = 32f
        setTransparent()
        setBorderless()

        +IconButton("animation.seek.start", "seek_start", 3f + 0f, 3f, 26f, 26f)
        +IconButton("animation.prev.keyframe", "prev_keyframe", 3f + 32f, 3f, 26f, 26f)
        +IconButton("animation.next.keyframe", "next_keyframe", 3f + 128f, 3f, 26f, 26f)
        +IconButton("animation.seek.end", "seek_end", 3f + 160f, 3f, 26f, 26f)
        if (props.animator.animationState == AnimationState.STOP) {
            +IconButton("animation.state.backward", "play_reversed", 3f + 64f, 3f, 26f, 26f)
            +IconButton("animation.state.forward", "play_normal", 3f + 96f, 3f, 26f, 26f)
        } else {
            +IconButton("animation.state.stop", "play_pause", 3f + 64f, 3f, 58f, 26f)
        }

        +FloatInput {
            FloatInput.Props(vec2Of(32f * 6, 4f), props.animator::animationSize, props.animator)
        }
    }

    data class Props(val visibleElements: VisibleElements, val modelAccessor: IModelAccessor, val animator: Animator)

    companion object : RComponentSpec<BottomPanel, BottomPanel.Props, Unit>
}