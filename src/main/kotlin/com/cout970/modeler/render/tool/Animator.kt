package com.cout970.modeler.render.tool

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.api.animation.*
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.toTRS
import com.cout970.modeler.gui.Gui
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times

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

    fun animate(anim: IAnimation, target: AnimationTarget, transform: ITransformation): ITransformation {
        val validChannels = anim.channels
                .filter { it.value.enabled }
                .filter { (chanRef) -> anim.channelMapping[chanRef] == target }
                .map { it.value }

        return animate(validChannels, transform)
    }

    fun animate(anim: IAnimation, group: IGroupRef, transform: ITransformation): ITransformation {
        val validChannels = anim.channels
                .filter { it.value.enabled }
                .filter { (chanRef) -> (anim.channelMapping[chanRef] as? AnimationTargetGroup)?.ref == group }
                .map { it.value }

        return animate(validChannels, transform)
    }

    fun animate(anim: IAnimation, obj: IObjectRef, transform: ITransformation): ITransformation {
        val validChannels = anim.channels
                .filter { it.value.enabled }
                .filter { (chanRef) -> (anim.channelMapping[chanRef] as? AnimationTargetObject)?.ref == obj }
                .map { it.value }

        return animate(validChannels, transform)
    }

    fun animate(validChannels: List<IChannel>, transform: ITransformation): ITransformation {
        val now = animationTime

        if (validChannels.isEmpty()) return transform

        val anim = validChannels.fold(TRSTransformation.IDENTITY as ITransformation) { acc, c ->
            val (prev, next) = getPrevAndNext(now, c.keyframes)
            acc + interpolate(now, prev, next)
        }

        return combine(transform, anim)
    }

    companion object {

        fun combine(original: ITransformation, animation: ITransformation): TRSTransformation {
            val old = original.toTRS()
            val new = animation.toTRS()

            return TRSTransformation(
                    translation = old.translation + new.translation,
                    rotation = old.rotation * new.rotation,
                    scale = old.scale + new.scale
            )
        }

        fun interpolate(time: Float, prev: IKeyframe, next: IKeyframe): ITransformation {
            if (next.time == prev.time) return next.value

            val size = next.time - prev.time
            val step = (time - prev.time) / size

            return interpolate(prev.value, next.value, step)
        }

        fun interpolate(a: ITransformation, b: ITransformation, delta: Float): ITransformation {
            return a.toTRS().lerp(b.toTRS(), delta)
        }

        fun getPrevAndNext(time: Float, keyframes: List<IKeyframe>): Pair<IKeyframe, IKeyframe> {
            val next = keyframes.firstOrNull { it.time > time } ?: keyframes.first()
            val prev = keyframes.lastOrNull { it.time <= time } ?: keyframes.last()

            return prev to next
        }
    }
}