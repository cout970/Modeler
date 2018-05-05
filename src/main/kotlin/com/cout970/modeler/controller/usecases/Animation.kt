package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.animation.AnimationState
import com.cout970.modeler.api.animation.IChannelRef
import com.cout970.modeler.api.animation.InterpolationMethod
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.ModifyGui
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateAnimation
import com.cout970.modeler.core.animation.Channel
import com.cout970.modeler.core.animation.Keyframe
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.render.tool.Animator
import org.liquidengine.legui.component.Component


private var lastAnimation = 0

@UseCase("animation.channel.add")
private fun addAnimationChannel(modelAccessor: IModelAccessor): ITask {
    val refs = modelAccessor.modelSelection.map { it.objects }.getOrNull() ?: return TaskNone
    val anim = modelAccessor.animation

    val newAnimation = anim.withChannel(
            Channel(
                    name = "Channel ${lastAnimation++}",
                    interpolation = InterpolationMethod.LINEAR,
                    keyframes = listOf(
                            Keyframe(0f, TRSTransformation.IDENTITY),
                            Keyframe(anim.timeLength, TRSTransformation.IDENTITY)
                    ),
                    objects = refs
            )
    )

    return TaskUpdateAnimation(modelAccessor.animation, newAnimation)
}


@UseCase("animation.channel.select")
private fun selectAnimationChannel(comp: Component): ITask = ModifyGui {
    it.animator.selectedChannel = comp.metadata["ref"] as IChannelRef
}


@UseCase("animation.channel.enable")
private fun enableAnimationChannel(comp: Component, modelAccessor: IModelAccessor): ITask {
    val animation = modelAccessor.animation
    val ref = comp.metadata["ref"] as IChannelRef
    val channel = animation.channels[ref]!!

    val newAnimation = animation.withChannel(channel.withEnable(true))

    return TaskUpdateAnimation(modelAccessor.animation, newAnimation)
}

@UseCase("animation.channel.disable")
private fun disableAnimationChannel(comp: Component, modelAccessor: IModelAccessor): ITask {
    val animation = modelAccessor.animation
    val ref = comp.metadata["ref"] as IChannelRef
    val channel = animation.channels[ref]!!

    val newAnimation = animation.withChannel(channel.withEnable(false))

    return TaskUpdateAnimation(modelAccessor.animation, newAnimation)
}

@UseCase("animation.channel.delete")
private fun removeAnimationChannel(comp: Component, modelAccessor: IModelAccessor): ITask {
    val animation = modelAccessor.animation
    val channel = comp.metadata["ref"] as IChannelRef
    val newAnimation = animation.removeChannels(listOf(channel))

    return TaskUpdateAnimation(modelAccessor.animation, newAnimation)
}


@UseCase("animation.state.toggle")
private fun animationTogglePlay(): ITask = ModifyGui {
    if (it.animator.animationState == AnimationState.STOP) {
        it.animator.animationState = AnimationState.FORWARD
    } else {
        it.animator.animationState = AnimationState.STOP
    }
}

@UseCase("animation.state.backward")
private fun animationPlayBackwards(): ITask = ModifyGui {
    it.animator.animationState = AnimationState.BACKWARD
}

@UseCase("animation.state.forward")
private fun animationPlayForward(): ITask = ModifyGui {
    it.animator.animationState = AnimationState.FORWARD
}

@UseCase("animation.state.stop")
private fun animationStop(): ITask = ModifyGui {
    it.animator.animationState = AnimationState.STOP
}

@UseCase("animation.seek.start")
private fun animationSeekStart(): ITask = ModifyGui {
    it.animator.animationTime = 0f
}

@UseCase("animation.seek.end")
private fun animationSeekEnd(): ITask = ModifyGui {
    it.animator.animationTime = it.modelAccessor.animation.timeLength
}