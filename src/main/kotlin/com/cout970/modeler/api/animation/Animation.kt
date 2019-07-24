package com.cout970.modeler.api.animation

import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.TRTSTransformation
import java.util.*

interface IAnimationRef {
    val id: UUID
}

sealed class AnimationTarget

data class AnimationTargetGroup(
    val ref: IGroupRef
) : AnimationTarget()

data class AnimationTargetObject(
    val refs: List<IObjectRef>
) : AnimationTarget()

interface IAnimation {
    val id: UUID
    val name: String
    val channels: Map<IChannelRef, IChannel>
    val channelMapping: Map<IChannelRef, AnimationTarget>
    val timeLength: Float

    fun withName(name: String): IAnimation

    fun withChannel(channel: IChannel): IAnimation

    fun withTimeLength(newLength: Float): IAnimation

    fun withMapping(channel: IChannelRef, target: AnimationTarget): IAnimation

    fun removeChannels(list: List<IChannelRef>): IAnimation

    operator fun plus(other: IAnimation): IAnimation
}

interface IChannelRef {
    val id: UUID
}

interface IChannel {
    val id: UUID
    val name: String
    val interpolation: InterpolationMethod
    val keyframes: List<IKeyframe>
    val enabled: Boolean
    val type: ChannelType

    fun withName(name: String): IChannel

    fun withEnable(enabled: Boolean): IChannel

    fun withInterpolation(method: InterpolationMethod): IChannel

    fun withKeyframes(keyframes: List<IKeyframe>): IChannel

    fun withType(type: ChannelType): IChannel
}

interface IKeyframe {
    val time: Float
    val value: TRTSTransformation

    fun withValue(trs: TRTSTransformation): IKeyframe
    fun withTime(time: Float): IKeyframe
}

enum class ChannelType {
    TRANSLATION, ROTATION, SCALE
}

enum class InterpolationMethod {
    LINEAR, COSINE, STEP
}

enum class AnimationState {
    STOP, FORWARD, BACKWARD
}