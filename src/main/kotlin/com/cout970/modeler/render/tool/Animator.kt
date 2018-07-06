package com.cout970.modeler.render.tool

import com.cout970.glutilities.structure.Timer
import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.api.animation.AnimationState
import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.animation.IChannelRef
import com.cout970.modeler.api.animation.IKeyframe
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.animation.ref
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.TRTSTransformation
import com.cout970.modeler.gui.Gui

class Animator {

    lateinit var gui: Gui

    var zoom = 1f
    var offset = 0f
    var animationTime = 0f

    var selectedChannel: IChannelRef? = null
        set(value) {
            field = value
            gui.listeners.onAnimatorChange(this)
            selectedKeyframe = null
        }

    var selectedKeyframe: Int? = null
        set(value) {
            field = value; gui.listeners.onAnimatorChange(this)
        }

    var animationState = AnimationState.STOP
        set(value) {
            field = value; gui.listeners.onAnimatorChange(this)
        }

    val animation get() = gui.programState.animation

    fun updateTime(timer: Timer) {
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

    fun animate(anim: IAnimation, obj: IObjectRef): IMatrix4 {

        val now = animationTime
        val activeChannels = anim.channels
                .values
                .filter { it.enabled }
                .filter { obj in anim.objectMapping[it.ref] }

        return activeChannels.fold(TRSTransformation.IDENTITY as ITransformation) { acc, c ->
            val (prev, next) = getPrevAndNext(now, c.keyframes)
            acc + interpolate(now, prev, next)
        }.matrix
    }

    fun interpolate(time: Float, prev: IKeyframe, next: IKeyframe): ITransformation {
        if (next.time == prev.time) return next.value

        val size = next.time - prev.time
        val step = (time - prev.time) / size

        return interpolate(prev.value, next.value, step)
    }

    fun interpolate(a: ITransformation, b: ITransformation, delta: Float): ITransformation {
        return when {
            a is TRSTransformation && b is TRSTransformation -> a.lerp(b, delta)
            a is TRTSTransformation && b is TRTSTransformation -> a.lerp(b, delta)
            a is TRTSTransformation && b is TRSTransformation -> a.lerp(b.toTRTS(), delta)
            a is TRTSTransformation && b is TRSTransformation -> a.lerp(b.toTRTS(), delta)
            else -> error("Unknown ITransformation pair: $a, $b")
        }
    }

    fun getPrevAndNext(time: Float, keyframes: List<IKeyframe>): Pair<IKeyframe, IKeyframe> {
        val next = keyframes.firstOrNull { it.time > time } ?: keyframes.first()
        val prev = keyframes.lastOrNull { it.time <= time } ?: keyframes.last()

        return prev to next
    }
}