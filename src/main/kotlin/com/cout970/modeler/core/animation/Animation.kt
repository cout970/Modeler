package com.cout970.modeler.core.animation

import com.cout970.modeler.api.animation.*
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.RootGroupRef
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
        require(target !is AnimationTargetGroup || target.ref != RootGroupRef) {
            "Cannot apply animation to the root group"
        }
        return copy(channelMapping = channelMapping + Pair(channel, target))
    }

    override fun removeChannels(list: List<IChannelRef>): IAnimation {
        return copy(channels = channels.filterKeys { it !in list })
    }

    override fun plus(other: IAnimation): IAnimation {
        return copy(channels = channels + other.channels)
    }

    companion object {
        fun of(
                name: String = "Animation",
                timeLength: Float = 1f,
                channels: Map<IChannelRef, IChannel> = emptyMap(),
                channelMapping: Map<IChannelRef, AnimationTarget> = emptyMap()
        ): IAnimation {
            return Animation(
                    channels = channels,
                    channelMapping = channelMapping,
                    name = name,
                    timeLength = timeLength
            )
        }
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

object AnimationNone : IAnimation {
    override val id: UUID get() = AnimationRefNone.id
    override val name: String get() = "None"
    override val channels: Map<IChannelRef, IChannel> get() = emptyMap()
    override val channelMapping: Map<IChannelRef, AnimationTarget> get() = emptyMap()
    override val timeLength: Float get() = 1f

    override fun withChannel(channel: IChannel): IAnimation = this

    override fun withTimeLength(newLength: Float): IAnimation = this

    override fun withMapping(channel: IChannelRef, target: AnimationTarget): IAnimation = this

    override fun removeChannels(list: List<IChannelRef>): IAnimation = this

    override fun plus(other: IAnimation): IAnimation = other
}