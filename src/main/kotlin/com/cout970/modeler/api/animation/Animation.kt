package com.cout970.modeler.api.animation

import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.`object`.Multimap
import java.util.*

interface IAnimationRef {
    val id: UUID
}

interface IAnimation {
    val id: UUID
    val name: String
    val channels: Map<IChannelRef, IChannel>
    val objectMapping: Multimap<IChannelRef, IObjectRef>
    val timeLength: Float

    fun withChannel(channel: IChannel): IAnimation
    fun withTimeLength(newLength: Float): IAnimation
    fun withMapping(channel: IChannelRef, objects: List<IObjectRef>): IAnimation

    fun removeChannels(list: List<IChannelRef>): IAnimation

    fun getChannels(obj: IObjectRef): List<IChannel>

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

    fun withName(name: String): IChannel
    fun withEnable(enabled: Boolean): IChannel
    fun withInterpolation(method: InterpolationMethod): IChannel
    fun withKeyframes(keyframes: List<IKeyframe>): IChannel
}

interface IKeyframe {
    val time: Float
    val value: ITransformation

    fun withValue(trs: ITransformation): IKeyframe
}

enum class InterpolationMethod {
    LINEAR, COSINE
}

enum class AnimationState {
    STOP, FORWARD, BACKWARD
}