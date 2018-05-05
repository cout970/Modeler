package com.cout970.modeler.render.tool

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.api.animation.AnimationState
import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.animation.IChannelRef
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

            val next = c.keyframes.firstOrNull { it.time > now } ?: c.keyframes.first()
            val prev = c.keyframes.lastOrNull { it.time <= now } ?: c.keyframes.last()

            val size = next.time - prev.time
            val step = (now - prev.time) / size

            val t = prev.value.lerp(next.value, step)

            acc.merge(t)
        }

        shader.matrixM.setMatrix4(m.matrix)
    }
}