package com.cout970.modeler.render.tool

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.api.animation.*
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.TRTSTransformation
import com.cout970.modeler.core.model.toTRTS
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.util.lerp
import com.cout970.modeler.util.slerp
import com.cout970.modeler.util.toAxisRotations
import com.cout970.modeler.util.toFrame
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.vec3Of
import kotlin.math.PI
import kotlin.math.cos

class Animator {

    lateinit var gui: Gui

    var zoom = 1f
    var offset = 0f
    var animationTime = 0f

    var selectedChannel: IChannelRef? = null
        set(value) {
            field = value
            selectedKeyframe = null
            sendUpdate()
        }

    var selectedKeyframe: Int? = null
        set(value) {
            field = value
            sendUpdate()
        }

    var animationState = AnimationState.STOP
        set(value) {
            field = value
            sendUpdate()
        }


    val selectedAnimation: IAnimationRef get() = gui.programState.selectedAnimation
    val animation get() = gui.programState.animation

    fun sendUpdate() {
        gui.listeners.runGuiCommand("updateAnimation")
    }

    fun updateTime(timer: Timer) {
        if (animationState != AnimationState.STOP && gui.state.modelSelection.isNonNull()) {
            gui.state.cursor.update(gui)
        }
        when (animationState) {
            AnimationState.FORWARD -> {
                animationTime += timer.delta.toFloat()
                animationTime %= animation.timeLength
            }
            AnimationState.BACKWARD -> {
                animationTime -= timer.delta.toFloat()
                if (animationTime < 0) {
                    animationTime += animation.timeLength
                }
            }
            else -> Unit
        }
    }

    fun animateGroup(anim: IAnimation, group: IGroupRef, transform: ITransformation): ITransformation {
        val validChannels = anim.channels
            .filter { it.value.enabled }
            .filter { (chanRef) -> (anim.channelMapping[chanRef] as? AnimationTargetGroup)?.ref == group }
            .map { it.value }

        return animateTransform(validChannels, transform.toTRTS())
    }

    fun animateObject(anim: IAnimation, obj: IObjectRef, transform: ITransformation): ITransformation {
        val validChannels = anim.channels
            .filter { it.value.enabled }
            .filter { (chanRef) -> (obj in (anim.channelMapping[chanRef] as? AnimationTargetObject)?.refs ?: emptyList()) }
            .map { it.value }

        return animateTransform(validChannels, transform.toTRTS())
    }

    fun animateTransform(validChannels: List<IChannel>, current: TRTSTransformation): TRTSTransformation {
        val now = animationTime

        if (validChannels.isEmpty()) return current
        val overrideProperties = mutableListOf<ChannelType>()

        val anim = validChannels.fold(TRTSTransformation.IDENTITY) { acc, channel ->
            val (prev, next) = getPrevAndNext(now, channel.keyframes)
            val combined = interpolateKeyframes(now, prev, next, channel.interpolation)

            overrideProperties += channel.type
            val focus = when (channel.type) {
                ChannelType.TRANSLATION -> TRTSTransformation(translation = combined.translation)
                ChannelType.ROTATION -> TRTSTransformation(rotation = combined.rotation, pivot = combined.pivot)
                ChannelType.SCALE -> TRTSTransformation(scale = combined.scale)
            }

            acc.merge(focus)
        }

        var transform = current
        if (ChannelType.ROTATION in overrideProperties) {
            transform = transform.merge(TRTSTransformation(
                rotation = anim.rotation,
                pivot = anim.pivot
            ))
        }

        val final = TRTSTransformation(
            if (ChannelType.TRANSLATION in overrideProperties) anim.translation else transform.translation,
            transform.rotation,
            transform.pivot,
            if (ChannelType.SCALE in overrideProperties) anim.scale else transform.scale
        )

//        val final = TRTSTransformation(
//            if (ChannelType.TRANSLATION in overrideProperties) anim.translation else transform.translation,
//            if (ChannelType.ROTATION in overrideProperties) anim.rotation else transform.rotation,
//            if (ChannelType.ROTATION in overrideProperties) anim.pivot else transform.pivot,
//            if (ChannelType.SCALE in overrideProperties) anim.scale else transform.scale
//        )

        return combine(transform, final)
    }

    companion object {

        @Suppress("UNUSED_PARAMETER")
        fun combine(original: TRTSTransformation, animation: TRTSTransformation): TRTSTransformation {
            // Change if needed a different algorithm
            return animation
        }

        fun interpolateKeyframes(time: Float, prev: IKeyframe, next: IKeyframe, method: InterpolationMethod): TRTSTransformation {
            if (next.time == prev.time) return next.value

            val size = next.time - prev.time
            val step = (time - prev.time) / size

            return interpolateTransforms(prev.value, next.value, step, method)
        }

        fun interpolateTransforms(a: TRTSTransformation, b: TRTSTransformation, delta: Float, method: InterpolationMethod): TRTSTransformation {
            val step = delta.toDouble()

            return TRTSTransformation(
                translation = interpolateVector3(a.translation, b.translation, step, method),
                rotation = interpolateQuaternion(a.quatRotation, b.quatRotation, step, method).toAxisRotations(),
                pivot = interpolateVector3(a.pivot, b.pivot, step, method),
                scale = interpolateVector3(a.scale, b.scale, step, method)
            )
        }

        fun interpolateVector3(a: IVector3, b: IVector3, mu: Double, method: InterpolationMethod): IVector3 {
            return when (method) {
                InterpolationMethod.LINEAR -> vec3Of(
                    linear(a.xd, b.xd, mu),
                    linear(a.yd, b.yd, mu),
                    linear(a.zd, b.zd, mu)
                )
                InterpolationMethod.COSINE -> vec3Of(
                    cosine(a.xd, b.xd, mu),
                    cosine(a.yd, b.yd, mu),
                    cosine(a.zd, b.zd, mu)
                )
                InterpolationMethod.STEP -> a
            }
        }

        fun interpolateQuaternion(a: IQuaternion, b: IQuaternion, mu: Double, method: InterpolationMethod): IQuaternion {
            return when (method) {
                InterpolationMethod.LINEAR -> a.lerp(b, mu)
                InterpolationMethod.COSINE -> a.slerp(b, mu)
                InterpolationMethod.STEP -> a
            }
        }

        fun linear(y1: Double, y2: Double, mu: Double): Double {
            return y1 * (1 - mu) + y2 * mu
        }

        fun cosine(y1: Double, y2: Double, mu: Double): Double {
            val mu2 = (1 - cos(mu * PI)) / 2
            return y1 * (1 - mu2) + y2 * mu2
        }

        fun getPrevAndNext(time: Float, keyframes: List<IKeyframe>): Pair<IKeyframe, IKeyframe> {
            val next = keyframes.firstOrNull { it.time.toFrame() > time.toFrame() } ?: keyframes.first()
            val prev = keyframes.lastOrNull { it.time.toFrame() <= time.toFrame() } ?: keyframes.last()

            return prev to next
        }
    }
}