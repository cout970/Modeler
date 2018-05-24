package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.animation.AnimationState
import com.cout970.modeler.api.animation.IChannelRef
import com.cout970.modeler.api.animation.InterpolationMethod
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.animation.Channel
import com.cout970.modeler.core.animation.Keyframe
import com.cout970.modeler.core.animation.ref
import com.cout970.modeler.core.model.TRTSTransformation
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.absolutePositionV
import com.cout970.reactive.dsl.width
import org.liquidengine.legui.component.Component
import kotlin.math.roundToInt


private var lastAnimation = 0

@UseCase("animation.channel.add")
private fun addAnimationChannel(modelAccessor: IModelAccessor): ITask {
    val refs = modelAccessor.modelSelection.map { it.objects }.getOrNull() ?: return TaskNone
    val anim = modelAccessor.animation

    val channel = Channel(
            name = "Channel ${lastAnimation++}",
            interpolation = InterpolationMethod.LINEAR,
            keyframes = listOf(
                    Keyframe(0f, TRTSTransformation.IDENTITY),
                    Keyframe(anim.timeLength, TRTSTransformation.IDENTITY)
            )
    )
    val newAnimation = anim.withChannel(channel).withMapping(channel.ref, refs)

    return TaskChain(listOf(
            TaskUpdateAnimation(modelAccessor.animation, newAnimation),
            ModifyGui { it.animator.selectedChannel = channel.ref }
    ))
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


@UseCase("animation.set.length")
private fun setAnimationLength(comp: Component, modelAccessor: IModelAccessor): ITask {
    val animation = modelAccessor.animation
    val time = comp.metadata["time"] as Float

    if (time <= 0) return TaskNone

    val newAnimation = animation.withTimeLength(time)

    return TaskUpdateAnimation(modelAccessor.animation, newAnimation)
}

@UseCase("animation.panel.click")
private fun onAnimationPanelClick(comp: Component, animator: Animator, input: IInput): ITask {
    val mousePos = input.mouse.getMousePos()
    val compPos = comp.absolutePositionV
    val diffX = mousePos.xf - compPos.xf
    val diffY = mousePos.yf - compPos.yf

    val zoom = animator.zoom
    val timeToPixel = comp.width / zoom
    val pixelOffset = animator.offset * timeToPixel

    val channels = animator.animation.channels.values
    val time = (diffX - pixelOffset) / timeToPixel
    val roundTime = (time * 60f).roundToInt() / 60f

    channels.forEachIndexed { i, channel ->
        if (diffY > i * 26 && diffY <= (i + 1) * 26f) {

            channel.keyframes.forEachIndexed { index, keyframe ->
                val pos = keyframe.time * timeToPixel + pixelOffset

                if (diffX > pos - 12f && diffX <= pos + 12f) {
                    return ModifyGui {
                        animator.selectedChannel = channel.ref
                        animator.selectedKeyframe = index
                        animator.animationTime = roundTime
                    }
                }
            }
        }
    }

    return ModifyGui {
        animator.selectedKeyframe = null
        animator.animationTime = roundTime
    }
}

@UseCase("animation.add.keyframe")
private fun addKeyframe(animator: Animator): ITask {
    val channelRef = animator.selectedChannel ?: return TaskNone
    val channel = animator.animation.channels[channelRef]!!

    val now = (animator.animationTime * 60f).toInt() / 60f
    if (channel.keyframes.any { it.time == now }) return TaskNone

    val prev = channel.keyframes.filter { it.time <= now }
    val next = channel.keyframes.filter { it.time >= now }

    val pair = animator.getPrevAndNext(now, channel.keyframes)
    val value = animator.interpolate(now, pair.first, pair.second)
    val keyframe = Keyframe(now, value)

    val newList = prev + keyframe + next
    val newChannel = channel.withKeyframes(newList)
    val newAnimation = animator.animation.withChannel(newChannel)

    return TaskChain(listOf(
            TaskUpdateAnimation(oldAnimation = animator.animation, newAnimation = newAnimation),
            ModifyGui { animator.selectedKeyframe = newList.indexOf(keyframe) }
    ))
}

@UseCase("animation.delete.keyframe")
private fun removeKeyframe(animator: Animator): ITask {
    val channelRef = animator.selectedChannel ?: return TaskNone
    val keyframeIndex = animator.selectedKeyframe ?: return TaskNone

    val channel = animator.animation.channels[channelRef]!!
    val keyframe = channel.keyframes[keyframeIndex]

    if (channel.keyframes.size <= 1) return TaskNone

    val newChannel = channel.withKeyframes(channel.keyframes - keyframe)
    val newAnimation = animator.animation.withChannel(newChannel)

    return TaskChain(listOf(
            ModifyGui { it.animator.selectedKeyframe = null },
            TaskUpdateAnimation(oldAnimation = animator.animation, newAnimation = newAnimation)
    ))
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

@UseCase("animation.prev.keyframe")
private fun prevKeyframe(animator: Animator): ITask {
    val selected = animator.selectedChannel ?: return TaskNone
    val channel = animator.animation.channels[selected]!!

    val prev = channel.keyframes.findLast { it.time < animator.animationTime } ?: return TaskNone

    return ModifyGui {
        it.animator.animationTime = prev.time
    }
}

@UseCase("animation.next.keyframe")
private fun nextKeyframe(animator: Animator): ITask {
    val selected = animator.selectedChannel ?: return TaskNone
    val channel = animator.animation.channels[selected]!!

    val next = channel.keyframes.find { it.time > animator.animationTime } ?: return TaskNone

    return ModifyGui {
        it.animator.animationTime = next.time
    }
}