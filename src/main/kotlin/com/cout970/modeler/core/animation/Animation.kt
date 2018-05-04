package com.cout970.modeler.core.animation

import com.cout970.modeler.api.animation.*
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import java.util.*

/**
 * Created by cout970 on 2017/08/20.
 */

data class Animation(
        override val channels: Map<IChannelRef, IChannel>,
        override val timeLength: Float
) : IAnimation {

    override fun addChannels(list: List<IChannel>): IAnimation {
        return copy(channels = channels + list.associateBy { ChannelRef(it.id) })
    }

    override fun removeChannels(list: List<IChannelRef>): IAnimation {
        return copy(channels = channels.filterKeys { it !in list })
    }

    override fun getChannels(obj: IObjectRef): List<IChannel> {
        return channels.values.filter { obj in it.objects }
    }
}

data class ChannelRef(override val id: UUID) : IChannelRef

data class Channel(
        override val name: String,
        override val interpolation: InterpolationMethod,
        override val keyframes: List<IKeyframe>,
        override val objects: List<IObjectRef>,
        override val id: UUID = UUID.randomUUID()
) : IChannel

data class Keyframe(
        override val time: Float,
        override val value: TRSTransformation
) : IKeyframe

inline val IChannel.ref get() = ChannelRef(id)

inline fun animationOf(vararg channels: IChannel, time: Float = 1f) = Animation(channels.associateBy { it.ref }, time)

operator fun IAnimation.plus(other: IAnimation) = addChannels(other.channels.values.toList())
