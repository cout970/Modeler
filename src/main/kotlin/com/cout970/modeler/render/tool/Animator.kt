package com.cout970.modeler.render.tool

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.api.animation.AnimationState
import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.animation.IChannelRef
import com.cout970.modeler.api.animation.IKeyframe
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.render.tool.shader.UniversalShader

class Animator {

    lateinit var gui: Gui

    var zoom = 1f
    var offset = 0f
    var animationTime = 0f

    var selectedChannel: IChannelRef? = null
        set(value) {
            field = value; gui.listeners.onAnimatorChange(this)
        }

    var selectedKeyframe: Int? = null
        set(value) {
            field = value; gui.listeners.onAnimatorChange(this)
        }

    var animationState = AnimationState.STOP
        set(value) {
            field = value; gui.listeners.onAnimatorChange(this)
        }

    val animation get() = gui.modelAccessor.animation

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

    fun animate(anim: IAnimation, obj: IObjectRef, shader: UniversalShader) {

        val now = animationTime
        val activeChannels = anim.channels.values.filter { obj in it.objects }

        val m = activeChannels.fold(TRSTransformation.IDENTITY) { acc, c ->
            val (prev, next) = getPrevAndNext(now, c.keyframes)
            acc.merge(interpolate(now, prev, next))
        }

        shader.matrixM.setMatrix4(m.matrix)
    }

    fun interpolate(time: Float, prev: IKeyframe, next: IKeyframe): TRSTransformation {
        val size = next.time - prev.time
        val step = (time - prev.time) / size

        return prev.value.lerp(next.value, step)
    }

    fun getPrevAndNext(time: Float, keyframes: List<IKeyframe>): Pair<IKeyframe, IKeyframe> {
        val next = keyframes.firstOrNull { it.time > time } ?: keyframes.first()
        val prev = keyframes.lastOrNull { it.time <= time } ?: keyframes.last()

        return prev to next
    }
}