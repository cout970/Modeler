package com.cout970.modeler.gui.rcomponents

import com.cout970.glutilities.device.Keyboard
import com.cout970.modeler.api.animation.AnimationState
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.animation.ref
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.components.AnimationPanel
import com.cout970.modeler.gui.components.AnimationPanelHead
import com.cout970.modeler.gui.event.EventAnimatorUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.disableInput
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.*
import org.joml.Vector2f
import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.ScrollEvent
import kotlin.math.max

data class BottomPanelProps(
        val visible: Boolean,
        val animator: Animator,
        val modelAccessor: IModelAccessor,
        val input: IInput,
        val disptcher: Dispatcher) : RProps

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

        div("Control bar") {
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

        div("Time bar") {
            style {
                posY = 32f
                height = 20f
                background { lightDarkColor }
                style.border = PixelBorder().apply { enableBottom = true }
            }

            postMount {
                width = parent.width
            }

            comp(AnimationPanelHead(props.animator, props.modelAccessor.animation)) {

                style {
                    transparent()
                }

                postMount {
                    posX = 200f
                    sizeX = parent.sizeX - 200f
                    sizeY = parent.sizeY
                }
            }
        }

        timeline()

        on<EventAnimatorUpdate> { rerender() }
        on<EventSelectionUpdate> { rerender() }
    }

    fun RBuilder.controlPanel() = div("Control panel") {
        style {
            height = 32f
            transparent()
            borderless()
        }

        postMount { fillX() }

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
                pos = Vector2f(32f * 6f + 60f, 4f),
                getter = { props.animator.animation.timeLength },
                setter = { props.disptcher.onEvent("animation.set.length", Panel().apply { metadata["time"] = it }) }
        ))

        +IconButton("animation.add.keyframe", "add_keyframe", 120f + 256f, 3f, 26f, 26f).apply {
            if(props.animator.selectedChannel == null) {
                disable()
                disableInput()
            }
            setTooltip("Add keyframe to the current position")
        }
    }

    fun RBuilder.channelList() = div("Channel list") {
        val anim = props.modelAccessor.animation

        style {
            width = 200f
            transparent()
            style.border = PixelBorder().apply { enableRight = true }
        }

        postMount { height = parent.height }

        anim.channels.values.forEachIndexed { index, c ->
            div {
                style {
                    posY = index * 24f
                    width = 200f
                    height = 24f

                    if (props.animator.selectedChannel == c.ref) {
                        background { lightBrightColor }
                    } else {
                        background { lightDarkColor }
                    }

                    style.border = PixelBorder().apply { enableBottom = true; enableRight = true }
                }

                +IconButton("animation.channel.select", "obj_type_cube", 0f, 0f, 24f, 24f).apply {
                    metadata += "ref" to c.ref
                }

                +TextButton("animation.channel.select", c.name, 24f, 0f, 172f - 24f - 24f - 2f, 24f).apply {
                    transparent()
                    borderless()
                    fontSize = 20f
                    horizontalAlign = HorizontalAlign.LEFT
                    textState.padding.x = 2f
                    metadata += "ref" to c.ref
                }

                val icon = if (c.enabled) "hideIcon" else "showIcon"
                val cmd = if (c.enabled) "animation.channel.disable" else "animation.channel.enable"
                val tooltip = if (c.enabled) "Disable channel" else "Enable channel"

                +IconButton(cmd, icon, 146f, 0f, 24f, 24f).apply {
                    transparent()
                    borderless()
                    metadata += "ref" to c.ref
                    setTooltip(tooltip)
                }

                +IconButton("animation.channel.delete", "deleteIcon", 172f, 0f, 24f, 24f).apply {
                    transparent()
                    borderless()
                    metadata += "ref" to c.ref
                    setTooltip("Delete channel")
                }
            }
        }
    }

    fun RBuilder.timeline() = scrollablePanel("Timeline") {
        val anim = props.modelAccessor.animation

        style {
            transparent()
            borderless()
        }

        postMount {
            posY = 52f
            posX = 0f
            sizeX = parent.sizeX
            sizeY = parent.sizeY - posY
        }

        horizontalScroll {
            style { hide() }
        }

        verticalScroll {
            style {
                rectCorners()
                style.minWidth = 16f
                style.bottom = 0f
                arrowColor = color { lightBrightColor }
                scrollColor = color { darkColor }
                visibleAmount = 50f
                backgroundColor { color { lightBrightColor } }
            }
        }

        viewport {
            postMount {
                style.right = 16f
                style.bottom = 0f
                listenerMap.clear(ScrollEvent::class.java)
            }
        }

        container {

            style {
                transparent()
                borderless()
            }

            postMount {
                sizeX = parent.parent.sizeX - 20f
                sizeY = max(250f, anim.channels.size * 26f)
            }

            channelList()

            comp(AnimationPanel(props.animator, anim)) {
                style {
                    background { darkColor }
                    posX = 200f
                    style.border = debugPixelBorder()
                }

                postMount {
                    width = parent.sizeX - posX
                    height = parent.sizeY - posY
                }

                onClick { props.disptcher.onEvent("animation.panel.click", it.targetComponent) }

                onScroll(this@BottomPanel::handleScroll)
            }
        }
    }

    fun handleScroll(it: ScrollEvent<Component>) {
        if (props.input.keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL)) {
            props.animator.zoom += when {
                it.yoffset < 0 -> 1 / 16f
                props.animator.zoom > 1 / 16f -> -1 / 16f
                else -> 0f
            }
        } else {
            props.animator.offset += it.yoffset.toFloat() * -1 / 64f
        }
    }
}