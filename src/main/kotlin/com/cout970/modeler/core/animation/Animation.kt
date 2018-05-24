package com.cout970.modeler.core.animation

import com.cout970.modeler.api.animation.*
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.`object`.Multimap
import com.cout970.modeler.core.model.`object`.emptyMultimap
import java.util.*

/**
 * Created by cout970 on 2017/08/20.
 */

data class AnimationRef(override val id: UUID) : IAnimationRef

data class Animation(
        override val channels: Map<IChannelRef, IChannel>,
        override val objectMapping: Multimap<IChannelRef, IObjectRef>,
        override val timeLength: Float,
        override val id: UUID = UUID.randomUUID()
) : IAnimation {

    override fun withChannel(channel: IChannel): IAnimation {
        return copy(channels = channels + (channel.ref to channel))
    }

    override fun withTimeLength(newLength: Float): IAnimation {
        return copy(timeLength = newLength)
    }

    override fun withMapping(channel: IChannelRef, objects: List<IObjectRef>): IAnimation {
        return copy(objectMapping = objectMapping.set(channel, objects))
    }

    override fun removeChannels(list: List<IChannelRef>): IAnimation {
        return copy(channels = channels.filterKeys { it !in list })
    }

    override fun getChannels(obj: IObjectRef): List<IChannel> {
        return objectMapping.filter { obj in it.second }.map { it.first }.map { channels[it]!! }
    }

    override fun plus(other: IAnimation): IAnimation {
        return copy(channels = channels + other.channels)
    }
}

data class ChannelRef(override val id: UUID) : IChannelRef

data class Channel(
        override val name: String,
        override val interpolation: InterpolationMethod,
        override val keyframes: List<IKeyframe>,
        override val enabled: Boolean = true,
        override val id: UUID = UUID.randomUUID()
) : IChannel {

    override fun withName(name: String): IChannel = copy(name = name)

    override fun withEnable(enabled: Boolean): IChannel = copy(enabled = enabled)

    override fun withInterpolation(method: InterpolationMethod): IChannel = copy(interpolation = interpolation)

    override fun withKeyframes(keyframes: List<IKeyframe>): IChannel = copy(keyframes = keyframes)
}

data class Keyframe(
        override val time: Float,
        override val value: ITransformation
) : IKeyframe {

    override fun withValue(trs: ITransformation): IKeyframe = copy(value = trs)
}

inline val IChannel.ref: IChannelRef get() = ChannelRef(id)
inline val IAnimation.ref: IAnimationRef get() = AnimationRef(id)

fun animationOf(vararg channels: IChannel, time: Float = 1f) =
        Animation(channels.associateBy { it.ref }, emptyMultimap(), time)
