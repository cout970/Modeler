package com.cout970.modeler.gui.rcomponents

import com.cout970.modeler.api.animation.AnimationState
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.components.AnimationPanel
import com.cout970.modeler.gui.event.EventAnimatorUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.render.tool.Animator
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.joml.Vector2f
import org.joml.Vector4f
import org.liquidengine.legui.component.optional.align.HorizontalAlign

data class BottomPanelProps(val visible: Boolean, val animator: Animator, val modelAccessor: IModelAccessor) : RProps

class BottomPanel : RStatelessComponent<BottomPanelProps>() {

    override fun RBuilder.render() = div("BottomPanel") {
        style {
            background { darkColor }
            borderless()
            if (!props.visible)
                hide()
        }

        postMount {
            val left = if (parent.child("LeftPanel")?.isEnabled == true) 288f else 0f
            val right = if (parent.child("RightPanel")?.isEnabled == true) 288f else 0f
            posX = left
            posY = parent.size.y - 200f
            sizeY = 200f
            sizeX = parent.sizeX - left - right
        }

        div {
            style {
                height = 32f
                background { darkestColor }
                borderless()
            }
            postMount {
                width = parent.width
            }

            controlPanel()
        }

        operationList()

        div {
            style {
                height = 24f
                posY = 32f
                background { darkColor }
                border { blackColor }
            }

            postMount {
                width = parent.width
            }
        }

        comp(AnimationPanel(props.animator, props.modelAccessor.animation)) {
            style {
                background { darkColor }
                posY = 32f + 24f
                posX = 100f
            }

            postMount {
                width = parent.sizeX - posX
                height = parent.sizeY - posY
            }
        }
        on<EventAnimatorUpdate> { rerender() }
    }

    fun RBuilder.controlPanel() = div("Control panel") {
        style {
            width = 32f * 6f + 120f
            height = 32f
            transparent()
            borderless()
        }

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

        child(TinyFloatInput::class, TinyFloatInputProps(
                pos = Vector2f(32f * 6f, 4f),
                getter = props.animator::animationSize,
                setter = props.animator::animationSize.setter
        ))
    }

    fun RBuilder.operationList() = div {
        style {
            posY = 32f + 24f
            height = 200f - 32f
            transparent()
        }

        postMount { width = parent.width }

        var index = 0
        val operations = props.modelAccessor.animation.operations.values

        operations.forEach { op ->
            div {
                style {
                    posY = index * 24f
                    width = 100f
                    height = 24f
                    background { greyColor }
                    style.border = PixelBorder().apply {
                        enableBottom = true
                        color = Vector4f(0f, 0f, 0f, 1f)
                    }
                }

                +TextButton(text = op.name, posX = 5f, sizeX = 100f).apply {
                    textState.horizontalAlign = HorizontalAlign.LEFT
                }
            }
            index++
        }
    }
}