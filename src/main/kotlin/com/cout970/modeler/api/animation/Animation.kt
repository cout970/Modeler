package com.cout970.modeler.api.animation

import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.TRSTransformation
import java.util.*

interface IAnimation {
    val channels: Map<IChannelRef, IChannel>
    val timeLength: Float

    fun addChannels(list: List<IChannel>): IAnimation
    fun removeChannels(list: List<IChannelRef>): IAnimation

    fun getChannels(obj: IObjectRef): List<IChannel>
}

interface IChannelRef {
    val id: UUID
}

interface IChannel {
    val id: UUID
    val name: String
    val interpolation: InterpolationMethod
    val keyframes: List<IKeyframe>
    val objects: List<IObjectRef>
}

interface IKeyframe {
    val time: Float
    val value: TRSTransformation
}

enum class InterpolationMethod {
    LINEAR, COSINE
}

enum class AnimationState {
    STOP, FORWARD, BACKWARD
}