package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.animation.AnimationNone
import com.cout970.modeler.core.animation.Keyframe
import com.cout970.modeler.core.helpers.AnimationHelper
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.EulerRotation
import com.cout970.vector.extensions.vec3Of
import org.liquidengine.legui.component.Component

@UseCase("animation.update.keyframe")
private fun changeKeyframe(comp: Component, animator: Animator, model: IModel): ITask {
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

    return TaskUpdateModel(model, model.modifyAnimation(newAnimation))
}

@UseCase("animation.add.keyframe")
private fun addKeyframe(animator: Animator, model: IModel): ITask {
    val channelRef = animator.selectedChannel ?: return TaskNone
    val channel = animator.animation.channels[channelRef] ?: return TaskNone

    val now = (animator.animationTime * 60f).toInt() / 60f
    if (channel.keyframes.any { it.time == now }) return TaskNone

    val prev = channel.keyframes.filter { it.time <= now }
    val next = channel.keyframes.filter { it.time >= now }

    val pair = Animator.getPrevAndNext(now, channel.keyframes)
    val value = Animator.interpolate(now, pair.first, pair.second)
    val keyframe = Keyframe(now, value)

    val newList = prev + keyframe + next
    val newChannel = channel.withKeyframes(newList)
    val newAnimation = animator.animation.withChannel(newChannel)

    return TaskChain(listOf(
            TaskUpdateModel(model, model.modifyAnimation(newAnimation)),
            ModifyGui { animator.selectedKeyframe = newList.indexOf(keyframe) }
    ))
}

@UseCase("animation.delete.keyframe")
private fun removeKeyframe(animator: Animator, model: IModel): ITask {
    val channelRef = animator.selectedChannel ?: return TaskNone
    val keyframeIndex = animator.selectedKeyframe ?: return TaskNone

    val channel = animator.animation.channels[channelRef]!!
    val keyframe = channel.keyframes[keyframeIndex]

    if (channel.keyframes.size <= 1) return TaskNone

    val newChannel = channel.withKeyframes(channel.keyframes - keyframe)
    val newAnimation = animator.animation.withChannel(newChannel)

    return TaskChain(listOf(
            ModifyGui { it.animator.selectedKeyframe = null },
            TaskUpdateModel(model, model.modifyAnimation(newAnimation))
    ))
}

@UseCase("keyframe.spread.translate.x")
private fun spreadTranslationX(model: IModel, animator: Animator): ITask {
    return spreadValue(model, animator) { old, new ->
        old.copy(translation = vec3Of(new.translation.xd, old.translation.yd, old.translation.zd))
    }
}

@UseCase("keyframe.spread.translate.y")
private fun spreadTranslationY(model: IModel, animator: Animator): ITask {
    return spreadValue(model, animator) { old, new ->
        old.copy(translation = vec3Of(old.translation.xd, new.translation.yd, old.translation.zd))
    }
}

@UseCase("keyframe.spread.translate.z")
private fun spreadTranslationZ(model: IModel, animator: Animator): ITask {
    return spreadValue(model, animator) { old, new ->
        old.copy(translation = vec3Of(old.translation.xd, old.translation.yd, new.translation.zd))
    }
}

@UseCase("keyframe.spread.rotation.x")
private fun spreadRotationX(model: IModel, animator: Animator): ITask {
    return spreadValue(model, animator) { old, new ->
        old.copy(rotation = EulerRotation(vec3Of(new.euler.angles.xd, old.euler.angles.yd, old.euler.angles.zd)))
    }
}

@UseCase("keyframe.spread.rotation.y")
private fun spreadRotationY(model: IModel, animator: Animator): ITask {
    return spreadValue(model, animator) { old, new ->
        old.copy(rotation = EulerRotation(vec3Of(old.euler.angles.xd, new.euler.angles.yd, old.euler.angles.zd)))
    }
}

@UseCase("keyframe.spread.rotation.z")
private fun spreadRotationZ(model: IModel, animator: Animator): ITask {
    return spreadValue(model, animator) { old, new ->
        old.copy(rotation = EulerRotation(vec3Of(old.euler.angles.xd, old.euler.angles.yd, new.euler.angles.zd)))
    }
}


@UseCase("keyframe.spread.scale.x")
private fun spreadScaleX(model: IModel, animator: Animator): ITask {
    return spreadValue(model, animator) { old, new ->
        old.copy(scale = vec3Of(new.scale.xd, old.scale.yd, old.scale.zd))
    }
}

@UseCase("keyframe.spread.scale.y")
private fun spreadScaleY(model: IModel, animator: Animator): ITask {
    return spreadValue(model, animator) { old, new ->
        old.copy(scale = vec3Of(old.scale.xd, new.scale.yd, old.scale.zd))
    }
}

@UseCase("keyframe.spread.scale.z")
private fun spreadScaleZ(model: IModel, animator: Animator): ITask {
    return spreadValue(model, animator) { old, new ->
        old.copy(scale = vec3Of(old.scale.xd, old.scale.yd, new.scale.zd))
    }
}

private fun spreadValue(model: IModel, animator: Animator, func: (TRSTransformation, TRSTransformation) -> TRSTransformation): ITask {
    val animation = animator.animation
    if (animation == AnimationNone) return TaskNone
    val channelRef = animator.selectedChannel ?: return TaskNone
    val keyframe = animator.selectedKeyframe ?: return TaskNone

    val value = animation.channels[channelRef]!!.keyframes[keyframe].value

    val newModel = AnimationHelper.editChannel(model, animator) {
        it.withValue(func(it.value, value))
    } ?: return TaskNone

    return TaskUpdateModel(model, newModel)
}
