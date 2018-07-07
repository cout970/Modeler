package com.cout970.modeler.core.animation

import com.cout970.modeler.api.animation.*
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.IGroupRef
import java.util.*

/**
 * Created by cout970 on 2017/08/20.
 */

data class AnimationRef(override val id: UUID) : IAnimationRef

object AnimationRefNone : IAnimationRef {
    override val id: UUID = UUID.fromString("94ca9fb4-bf93-4423-b27a-6b7320b1727a")
}

data class Animation(
        override val channels: Map<IChannelRef, IChannel>,
        override val channelMapping: Map<IChannelRef, AnimationTarget>,
        override val timeLength: Float,
        override val name: String,
        override val id: UUID = UUID.randomUUID()
) : IAnimation {

    override fun withChannel(channel: IChannel): IAnimation {
        return copy(channels = channels + (channel.ref to channel))
    }

    override fun withTimeLength(newLength: Float): IAnimation {
        return copy(timeLength = newLength)
    }

    override fun withMapping(channel: IChannelRef, target: AnimationTarget): IAnimation {
        return copy(channelMapping = channelMapping + Pair(channel, target))
    }

    override fun removeChannels(list: List<IChannelRef>): IAnimation {
        return copy(channels = channels.filterKeys { it !in list })
    }

    override fun getChannels(group: IGroupRef): List<IChannel> {
        return channelMapping.filter { group == it.value }.map { it.key }.map { channels[it]!! }
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

// TODO
fun animationOf(vararg channels: IChannel, time: Float = 1f) =
        Animation(channels.associateBy { it.ref }, emptyMap(), time, "Animation")
