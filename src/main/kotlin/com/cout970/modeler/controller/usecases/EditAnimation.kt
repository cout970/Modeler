package com.cout970.modeler.controller.usecases

import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateAnimation
import com.cout970.modeler.render.tool.Animator
import org.liquidengine.legui.component.Component


@UseCase("animation.update.keyframe")
private fun changeKeyframe(comp: Component, animator: Animator): ITask {
    val offset = comp.metadata["offset"] as? Float ?: return TaskNone
    val cmd = comp.metadata["command"] as? String ?: return TaskNone
    val text = comp.metadata["content"] as? String ?: return TaskNone

    val channelRef = animator.selectedChannel ?: return TaskNone
    val keyframeIndex = animator.selectedKeyframe ?: return TaskNone

    val channel = animator.animation.channels[channelRef]!!
    val keyframe = channel.keyframes[keyframeIndex]

    val prev = channel.keyframes.filter { it.time < keyframe.time }
    val next = channel.keyframes.filter { it.time > keyframe.time }

    val newValue = updateTransformation(keyframe.value, cmd, text, offset) ?: return TaskNone
    val newKeyframe = keyframe.withValue(newValue)
    val newChannel = channel.withKeyframes(prev + newKeyframe + next)
    val newAnimation = animator.animation.withChannel(newChannel)

    return TaskUpdateAnimation(oldAnimation = animator.animation, newAnimation = newAnimation)
}

