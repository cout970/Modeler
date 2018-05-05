package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.animation.IKeyframe
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateAnimation
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.quatOfAngles
import com.cout970.modeler.util.toAxisRotations
import com.cout970.vector.extensions.vec3Of
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

    val newKeyframe = updateKeyframe(keyframe, cmd, text, offset) ?: return TaskNone
    val newChannel = channel.withKeyframes(prev + newKeyframe + next)
    val newAnimation = animator.animation.withChannel(newChannel)

    return TaskUpdateAnimation(oldAnimation = animator.animation, newAnimation = newAnimation)
}

private fun updateKeyframe(keyframe: IKeyframe, cmd: String, input: String, offset: Float): IKeyframe? {
    val value = keyframe.value
    val newValue: TRSTransformation = when (cmd) {
    //@formatter:off
            "keyframe.size.x" -> setSizeX(value, x = getValue(input, value.scale.xf) + offset)
            "keyframe.size.y" -> setSizeY(value, y = getValue(input, value.scale.yf) + offset)
            "keyframe.size.z" -> setSizeZ(value, z = getValue(input, value.scale.zf) + offset)

            "keyframe.pos.x" -> setPosX(value, x = getValue(input, value.translation.xf) + offset)
            "keyframe.pos.y" -> setPosY(value, y = getValue(input, value.translation.yf) + offset)
            "keyframe.pos.z" -> setPosZ(value, z = getValue(input, value.translation.zf) + offset)

            "keyframe.rot.x" -> setRotationX(value, x = getValue(input, value.rotation.toAxisRotations().xf) + offset * 15f)
            "keyframe.rot.y" -> setRotationY(value, y = getValue(input, value.rotation.toAxisRotations().yf) + offset * 15f)
            "keyframe.rot.z" -> setRotationZ(value, z = getValue(input, value.rotation.toAxisRotations().zf) + offset * 15f)

            else -> value
        //@formatter:on
    }
    if (value == newValue) {
        return null
    }
    return keyframe.withValue(newValue)
}

private fun setSizeX(cube: TRSTransformation, x: Float): TRSTransformation {
    return cube.copy(scale = vec3Of(x, cube.scale.y, cube.scale.z))
}

private fun setSizeY(cube: TRSTransformation, y: Float): TRSTransformation {
    return cube.copy(scale = vec3Of(cube.scale.x, y, cube.scale.z))
}

private fun setSizeZ(cube: TRSTransformation, z: Float): TRSTransformation {
    return cube.copy(scale = vec3Of(cube.scale.x, cube.scale.y, z))
}

private fun setPosX(cube: TRSTransformation, x: Float): TRSTransformation {
    return cube.copy(translation = vec3Of(x, cube.translation.y, cube.translation.z))
}

private fun setPosY(cube: TRSTransformation, y: Float): TRSTransformation {
    return cube.copy(translation = vec3Of(cube.translation.x, y, cube.translation.z))
}

private fun setPosZ(cube: TRSTransformation, z: Float): TRSTransformation {
    return cube.copy(translation = vec3Of(cube.translation.x, cube.translation.y, z))
}

private fun setRotationX(cube: TRSTransformation, x: Float): TRSTransformation {
    val oldRot = cube.rotation.toAxisRotations()
    return cube.copy(rotation = quatOfAngles(x.clampRot(), oldRot.y, oldRot.z))
}

private fun setRotationY(cube: TRSTransformation, y: Float): TRSTransformation {
    val oldRot = cube.rotation.toAxisRotations()

    return cube.copy(rotation = quatOfAngles(oldRot.x, y.clampRot(), oldRot.z))
}

private fun setRotationZ(cube: TRSTransformation, z: Float): TRSTransformation {
    val oldRot = cube.rotation.toAxisRotations()
    return cube.copy(rotation = quatOfAngles(oldRot.x, oldRot.y, z.clampRot()))
}

private fun Float.clampRot(): Double {
    return when {
        this > 180f -> this - 360f
        this < -180f -> this + 360f
        else -> this
    }.toDouble()
}

private fun getValue(input: String, default: Float): Float {
    return try {
        (scriptEngine.eval(input) as? Number)?.toFloat() ?: default
    } catch (e: Exception) {
        default
    }
}