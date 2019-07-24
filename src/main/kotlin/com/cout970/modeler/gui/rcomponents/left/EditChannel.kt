package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.api.animation.AnimationTargetGroup
import com.cout970.modeler.api.animation.AnimationTargetObject
import com.cout970.modeler.api.animation.ChannelType
import com.cout970.modeler.api.animation.InterpolationMethod
import com.cout970.modeler.controller.Dispatch
import com.cout970.modeler.core.animation.ref
import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.render.tool.Animator
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.*
import org.joml.Vector2f
import org.liquidengine.legui.component.CheckBox
import org.liquidengine.legui.component.event.selectbox.SelectBoxChangeSelectionEvent
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.CharIcon

data class EditChannelProps(val animator: Animator, val programState: IProgramState) : RProps

class EditChannel : RComponent<EditChannelProps, VisibleWidget>() {

    val enable: Boolean get() = props.animator.selectedChannel != null

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditChannel") {
        val channelRef = props.animator.selectedChannel
        val animation = props.animator.animation
        val channel = props.animator.animation.channels[channelRef]

        style {
            classes("left_panel_group", "edit_animation")
            height = if (state.on && channel != null) 300f else 24f
        }

        postMount {
            marginX(5f)
            alignAsColumn(8f, 16f)
        }

        child(GroupTitle::class.java, GroupTitleProps("Edit Channel", state.on) { setState { copy(on = !on) } })

        onCmd("updateModel") { rerender() }
        onCmd("updateSelection") { rerender() }
        onCmd("updateAnimation") { rerender() }

        if (channel == null) return@div

        comp(StringInput("animation.channel.rename", channel.name)) {
            style {
                metadata["ref"] = channel.ref
                classes("string_input", "string_input_rename")
                textState.horizontalAlign = HorizontalAlign.CENTER
                textState.fontSize = 24f
                sizeY = 32f
            }
            postMount {
                marginX(5f)
            }
        }

        +CheckBox("Enable channel", 10f, 0f, 260f, 24f).apply {
            defaultTextColor()
            classes("checkbox")
            isChecked = channel.enabled
            if (isChecked) classes("checkbox_active")

            configIcon(iconChecked as CharIcon)
            configIcon(iconUnchecked as CharIcon)

            postMount {
                marginX(5f)
            }

            onClick {
                val event = if (channel.enabled) "animation.channel.disable" else "animation.channel.enable"
                Dispatch.run(event) { this["ref"] = channel.ref }
                rerender()
            }
        }

        label("Channel type") {
            style { classes("inputLabel", "center_text") }
            postMount { marginX(5f) }
        }

        selectBox {
            style {
                sizeY = 26f
                addElement(ChannelType.TRANSLATION.toString())
                addElement(ChannelType.ROTATION.toString())
                addElement(ChannelType.SCALE.toString())
                setSelected(channel.type.ordinal, true)
            }
            postMount {
                marginX(5f)
            }

            on<SelectBoxChangeSelectionEvent<String>> { event ->
                val type = ChannelType.valueOf(event.newValue)
                Dispatch.run("animation.channel.type") {
                    this["ref"] = channel.ref
                    this["type"] = type
                }
            }
        }

        label("Interpolation method") {
            style { classes("inputLabel", "center_text") }
            postMount { marginX(5f) }
        }

        selectBox {
            style {
                sizeY = 26f
                addElement(InterpolationMethod.LINEAR.toString())
                addElement(InterpolationMethod.COSINE.toString())
                addElement(InterpolationMethod.STEP.toString())
                setSelected(channel.interpolation.ordinal, true)
            }
            postMount {
                marginX(5f)
            }

            on<SelectBoxChangeSelectionEvent<String>> { event ->
                val type = InterpolationMethod.valueOf(event.newValue)
                Dispatch.run("animation.channel.interpolation") {
                    this["ref"] = channel.ref
                    this["type"] = type
                }
            }
        }

        val targets = animation.channelMapping[channelRef] ?: AnimationTargetObject(listOf())
        val msg = when (targets) {
            is AnimationTargetObject -> if (targets.refs.size > 1) "Linked to ${targets.refs.size} objects" else "Linked to an object"
            is AnimationTargetGroup -> "Linked to a group"
        }

        button(msg) {
            style {
                classes("btn_text", "big_text")
                sizeY = 32f
            }
            postMount {
                marginX(5f)
            }

            onClick {
                Dispatch.run("animation.channel.select") {
                    this["ref"] = channelRef
                }
            }
        }

        button("Update with selection") {
            style {
                classes("btn_text", "big_text")
                sizeY = 32f
            }
            postMount {
                marginX(5f)
            }
            onClick {
                Dispatch.run("animation.channel.update") {
                    this["ref"] = channelRef
                }
            }
        }
    }

    private fun CheckBox.configIcon(icon: CharIcon) {
        classes("checkbox_icon_on")
        icon.size = Vector2f(24f, 24f)
        icon.position = Vector2f(size.x - icon.size.x, 0f)
        icon.horizontalAlign = HorizontalAlign.CENTER
    }
}