package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.core.model.TRTSTransformation
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.event.EventAnimatorUpdate
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.rcomponents.TransformationInput
import com.cout970.modeler.gui.rcomponents.TransformationInputProps
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.getOr
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.joml.Vector2f
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
                    textState.text = "Animation"
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

        div("Button list") {
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

            // TODO: select interpolation type for the selected channel
        }

        val channelRef = props.animator.selectedChannel
        val keyframeRef = props.animator.selectedKeyframe

        val channel = props.animator.animation.channels[channelRef]
        val keyframe = keyframeRef?.let { channel?.keyframes?.get(it) }.asNullable()
        val value = keyframe.map { it.value }.getOr(TRTSTransformation.IDENTITY)

        child(TransformationInput::class, TransformationInputProps(
                usecase = "animation.update.keyframe",
                transformation = value,
                enable = props.animator.selectedKeyframe != null
        ))

        on<EventModelUpdate> { rerender() }
        on<EventSelectionUpdate> { rerender() }
        on<EventAnimatorUpdate> { rerender() }
    }
}
