package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.*
import com.cout970.modeler.core.model.`object`.ObjectCube
import com.cout970.modeler.core.model.selection.ObjectRefNone
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.core.project.ModelAccessor
import com.cout970.modeler.gui.event.EventAnimatorUpdate
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.rcomponents.FloatInput
import com.cout970.modeler.gui.rcomponents.FloatInputProps
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.*
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.*
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.Vector2
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec2Of
import org.apache.commons.collections4.MapUtils.getObject
import org.joml.Vector2f
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.CharIcon
import org.liquidengine.legui.style.color.ColorConstants
import org.liquidengine.legui.style.font.FontRegistry


data class EditAnimationProps(val animator: Animator, val modelAccessor: IModelAccessor) : RProps

class EditAnimation : RComponent<EditAnimationProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditAnimation") {
        style {
            transparent()
            border(2f) { greyColor }
            rectCorners()
            height = if (state.on) 486f else 24f
        }

        postMount {
            marginX(5f)
            alignAsColumn(5f)
        }

        on<EventModelUpdate> {
            rerender()
        }
        on<EventSelectionUpdate> {
            rerender()
        }

        comp(FixedLabel()) {
            style {
                textState.apply {
                    this.text = "Animation"
                    textColor = Config.colorPalette.textColor.toColor()
                    horizontalAlign = HorizontalAlign.CENTER
                    fontSize = 20f
                }
            }

            postMount {
                posX = 50f
                posY = 0f
                sizeX = parent.sizeX - 100f
                sizeY = 24f
            }
        }

        // close button
        comp(IconButton()) {
            style {
                val charCode = if (state.on) 'X' else 'O'
                setImage(CharIcon(Vector2f(16f, 16f), FontRegistry.DEFAULT, charCode, ColorConstants.lightGray()))
                background { darkColor }
                posX = 250f
                posY = 4f
            }
            onRelease {
                setState { copy(on = !on) }
            }
        }

        // add channel button
        // select interpolation type for the selected channel
        div {
            style {
                transparent()
                borderless()
                posY = 24f
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
        }

        val channelRef = props.animator.selectedChannel
        val keyframeRef = props.animator.selectedKeyframe

        val channel = props.animator.animation.channels[channelRef]
        val keyframe = keyframeRef?.let { channel?.keyframes?.get(it) }.asNullable()
        val value = keyframe.map { it.value }

        val pos = { value.map { it.translation }.getOr(Vector3.ORIGIN) }
        val rotation = { value.map { it.rotation.toAxisRotations() }.getOr(Vector3.ORIGIN) }
        val size = { value.map { it.scale }.getOr(Vector3.ORIGIN) }

        div("Title") {
            style {
                transparent()
                borderless()
                sizeY = 24f
                posY = 1f
            }

            postMount {
                fillX()
            }

            comp(FixedLabel()) {
                style {
                    textState.text = "Edit keyframe"
                    fontSize = 22f
                    posX = 50f
                    posY = 0f
                    sizeY = 22f
                }

                postMount {
                    sizeX = parent.sizeX - 100
                }
            }

            // close button
            +IconButton(posX = 250f, posY = 3f).apply {
                val charCode = if (state.on) 'X' else 'O'
                setImage(CharIcon(Vector2f(16f, 16f), FontRegistry.DEFAULT, charCode, ColorConstants.lightGray()))
                background { darkColor }

                onRelease { setState { copy(on = !on) } }
            }
        }

        div("Size") {
            style {
                transparent()
                borderless()
                height = 110f
            }

            postMount {
                fillX()
            }

            +FixedLabel("Size", 0f, 0f, 278f, 18f).apply { fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { fontSize = 18f }

            valueInput({ size().xf }, "keyframe.size.x", vec2Of(10f, 20f))
            valueInput({ size().yf }, "keyframe.size.y", vec2Of(98f, 20f))
            valueInput({ size().zf }, "keyframe.size.z", vec2Of(185f, 20f))
        }

        div("Position") {
            style {
                transparent()
                borderless()
                height = 110f
            }

            postMount {
                fillX()
            }

            +FixedLabel("Position", 0f, 0f, 278f, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput({ pos().xf }, "keyframe.pos.x", vec2Of(10f, 20f))
            valueInput({ pos().yf }, "keyframe.pos.y", vec2Of(98f, 20f))
            valueInput({ pos().zf }, "keyframe.pos.z", vec2Of(185f, 20f))
        }

        div("Rotation") {
            style {
                transparent()
                borderless()
                height = 110f
            }

            postMount {
                fillX()
            }

            +FixedLabel("Rotation", 0f, 0f, 278f, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput({ rotation().xf }, "keyframe.rot.x", vec2Of(10f, 20f))
            valueInput({ rotation().yf }, "keyframe.rot.y", vec2Of(98f, 20f))
            valueInput({ rotation().zf }, "keyframe.rot.z", vec2Of(185f, 20f))
        }

        on<EventModelUpdate> { rerender() }
        on<EventSelectionUpdate> { rerender() }
        on<EventAnimatorUpdate> { rerender() }
    }


    fun DivBuilder.valueInput(getter: () -> Float, cmd: String, pos: IVector2) {
        child(FloatInput::class, FloatInputProps(
                getter = getter,
                command = "animation.update.keyframe",
                metadata = mapOf("command" to cmd),
                enabled = props.animator.selectedKeyframe != null,
                pos = pos)
        )
    }
}
