package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.core.model.TRTSTransformation
import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.gui.leguicomp.alignAsColumn
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.gui.leguicomp.onCmd
import com.cout970.modeler.gui.rcomponents.TransformationInput
import com.cout970.modeler.gui.rcomponents.TransformationInputProps
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.getOr
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.dsl.height
import com.cout970.reactive.dsl.marginX
import com.cout970.reactive.dsl.postMount
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style


data class EditKeyframeProps(val animator: Animator, val programState: IProgramState) : RProps

class EditKeyframe : RComponent<EditKeyframeProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(false)

    override fun RBuilder.render() = div("EditAnimation") {
        style {
            classes("left_panel_group", "edit_animation")
            height = if (state.on) 420f else 24f
        }

        postMount {
            marginX(5f)
            alignAsColumn(5f, 16f)
        }

        child(GroupTitle::class.java, GroupTitleProps("Edit Keyframe", state.on) { setState { copy(on = !on) } })

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

        onCmd("updateModel") { rerender() }
        onCmd("updateSelection") { rerender() }
        onCmd("updateAnimation") { rerender() }
    }
}
