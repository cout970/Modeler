package com.cout970.modeler.gui.rcomponents

import com.cout970.glutilities.device.Keyboard
import com.cout970.modeler.api.animation.AnimationState
import com.cout970.modeler.controller.Dispatch
import com.cout970.modeler.core.animation.AnimationNone
import com.cout970.modeler.core.animation.AnimationRefNone
import com.cout970.modeler.core.animation.ref
import com.cout970.modeler.core.project.IProgramState
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
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.event.selectbox.SelectBoxChangeSelectionEvent
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.ScrollEvent
import kotlin.math.max
import kotlin.math.min

data class BottomPanelProps(
    val visible: Boolean,
    val animator: Animator,
    val programState: IProgramState,
    val input: IInput) : RProps

class BottomPanel : RStatelessComponent<BottomPanelProps>() {

    override fun RBuilder.render() = div("BottomPanel") {

        style {
            classes("bottom_panel")
            if (!props.visible)
                classes("bottom_panel_hide")
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
                classes("bottom_panel_controls")
            }
            postMount {
                width = parent.width
            }

            controlPanel()
        }

        div("Time bar") {
            style {
                posY = 32f
                height = 24f
                classes("bottom_panel_time_bar")
            }

            postMount {
                width = parent.width
            }

            div("Button list") {
                style {
                    transparent()
                    borderless()
                    sizeY = 24f
                }

                postMount {
                    marginX(5f)
                    floatLeft(5f, 0f)
                }

                +IconButton("animation.channel.add", "add_channel", 0f, 0f, 24f, 24f).apply {
                    borderless()
                    rectCorners()
                    tooltip = InstantTooltip("Add new animation channel")
                }

                +IconButton("animation.channel.remove", "remove_channel", 0f, 0f, 24f, 24f).apply {
                    borderless()
                    rectCorners()
                    tooltip = InstantTooltip("Remove animation channel")
                }
            }

            comp(AnimationPanelHead(props.animator, props.programState.animation)) {

                style {
                    classes("bottom_panel_time_bar_left")
                }

                postMount {
                    posX = 200f
                    posY = 2f
                    sizeX = parent.sizeX - 200f
                    sizeY = parent.sizeY - 4f
                }
            }
        }

        timeline()

        onCmd("updateSelection") { rerender() }
        onCmd("updateAnimation") { rerender() }
    }

    fun RBuilder.controlPanel() = div("Control panel") {
        style {
            height = 32f
            transparent()
            borderless()
        }

        postMount {
            fillX()
            floatLeft(5f, 5f)
        }

        +IconButton("animation.seek.start", "seek_start", 0f, 3f, 26f, 26f)
        +IconButton("animation.prev.keyframe", "prev_keyframe", 0f, 3f, 26f, 26f)

        if (props.animator.animationState == AnimationState.STOP) {
            +IconButton("animation.state.backward", "play_reversed", 0f, 3f, 26f, 26f)
            +IconButton("animation.state.forward", "play_normal", 0f, 3f, 26f, 26f)
        } else {
            +IconButton("animation.state.stop", "play_pause", 0f, 3f, 58f, 26f)
        }

        +IconButton("animation.next.keyframe", "next_keyframe", 0f, 3f, 26f, 26f)
        +IconButton("animation.seek.end", "seek_end", 0f, 3f, 26f, 26f)

        child(TinyFloatInput::class, TinyFloatInputProps(
            pos = Vector2f(0f, 4f),
            getter = { props.animator.animation.timeLength },
            setter = { Dispatch.run("animation.set.length") { this["time"] = it } }
        ))

        +IconButton("animation.add.keyframe", "add_keyframe", 0f, 3f, 26f, 26f).apply {
            if (props.animator.selectedChannel == null) {
                disable()
                disableInput()
            }
            setTooltip("Add keyframe to the current position")
        }

        +IconButton("animation.delete.keyframe", "remove_keyframe", 0f, 3f, 26f, 26f).apply {
            if (props.animator.selectedKeyframe == null) {
                disable()
                disableInput()
            }
            setTooltip("Remove selected keyframe")
        }

        selectBox {
            val animations = props.programState.model.animationMap.values.toList()

            style {
                posY = 3f
                sizeX = 160f
                sizeY = 26f

                addElement("None")
                animations.forEachIndexed { index, animation -> addElement("$index ${animation.name}") }

                val selectedIndex = animations.indexOfFirst { it.ref == props.programState.selectedAnimation }
                if (selectedIndex != -1) {
                    setSelected(selectedIndex + 1, true)
                } else {
                    setSelected(0, true)
                }

                visibleCount = min(elements.size, 4)
                elementHeight = 26f
                classes("animation_selector")
                childComponents.forEach {
                    it.classes("animation_selector_item")
                }
            }

            on<SelectBoxChangeSelectionEvent<String>> { event ->
                val ref = if (event.newValue == "None") {
                    AnimationRefNone
                } else {
                    val selected = event.targetComponent.selectBoxElements
                        .indexOfFirst { it.`object` == event.newValue } - 1

                    if (selected in animations.indices) {
                        animations[selected].ref
                    } else {
                        AnimationRefNone
                    }
                }

                Dispatch.run("animation.select") { this["animation"] = ref }
            }
        }

        +IconButton("animation.add", "add_animation", 0f, 3f, 26f, 26f).apply {
            setTooltip("Add animation")
        }

        +IconButton("animation.dup", "dup_animation", 0f, 3f, 26f, 26f).apply {
            setTooltip("Duplicate animation")
        }

        +IconButton("animation.remove", "remove_animation", 0f, 3f, 26f, 26f).apply {
            setTooltip("Remove animation")
        }

        +StringInput("animation.rename", props.animator.animation.name, 0f, 3f, 160f, 26f).apply {
            if (props.animator.animation == AnimationNone) {
                disableInput()
                classes("string_input_disabled")
            }
        }
    }

    fun RBuilder.channelList() = div("Channel list") {
        val anim = props.programState.animation

        style {
            width = 200f
            classes("animation_track")
        }

        postMount { height = parent.height }

        anim.channels.values.forEachIndexed { index, c ->
            div {
                style {
                    posY = index * 24f
                    width = 200f
                    height = 24f
                    classes("animation_track_item")

                    if (props.animator.selectedChannel == c.ref) {
                        classes("animation_track_item_selected")
                    }
                }

                +IconButton("animation.channel.select", "obj_type_cube", 0f, 0f, 24f, 24f).apply {
                    metadata += "ref" to c.ref
                }

                +TextButton("animation.channel.select", c.name, 24f, 0f, 172f - 24f - 24f - 2f, 24f).apply {
                    transparent()
                    borderless()
                    fontSize = 20f
                    horizontalAlign = HorizontalAlign.LEFT
                    paddingLeft(2f)
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
        val anim = props.programState.animation

        style {
            classes("bottom_panel_timeline")
        }

        postMount {
            posY = 52f + 6f
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
                arrowColor = color { bright1 }
                scrollColor = color { dark2 }
                visibleAmount = 50f
                backgroundColor { color { bright1 } }
            }
        }

        viewport {
            style {
                transparent()
                borderless()
            }

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
                    posX = 200f
                    classes("animation_panel")
                }

                postMount {
                    width = parent.sizeX - posX
                    height = parent.sizeY - posY
                }

                onClick { Dispatch.run("animation.panel.click", it.targetComponent) }
                onKey {
                    if (it.action == 0) return@onKey

                    val state = when {
                        it.key == 263 -> "left"
                        it.key == 262 -> "right"
                        else -> return@onKey
                    }

                    Dispatch.run("animation.panel.key") {
                        this["key"] = state
                    }
                }
                onScroll(this@BottomPanel::handleScroll)
            }
        }
    }

    fun handleScroll(it: ScrollEvent<Component>) {
        if (props.input.keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL)) {
            val add = when {
                it.yoffset < 0 -> 1 / 16f
                props.animator.zoom > 1 / 16f -> -1 / 16f
                else -> 0f
            }
            props.animator.zoom += add
            props.animator.offset += add * 0.5f
        } else {
            props.animator.offset += it.yoffset.toFloat() * props.animator.zoom / 64f
        }
    }
}