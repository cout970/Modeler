package com.cout970.modeler.render.tool

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.api.animation.*
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.render.tool.shader.UniversalShader
import com.cout970.modeler.util.lerp
import com.cout970.modeler.util.reduceAll
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times

class Animator {

    lateinit var gui: Gui

    var animationTime = 0f

    var animationState = AnimationState.STOP
        set(value) {
            gui.listeners.onAnimatorChange(this); field = value
        }
    var animationSize = 1f
        set(value) {
            gui.listeners.onAnimatorChange(this); field = value
        }

    fun updateTime(timer: Timer) {
        when (animationState) {
            AnimationState.FORWARD -> {
                animationTime += timer.delta.toFloat()
                animationTime %= animationSize
            }
            AnimationState.BACKWARD -> {
                animationTime -= timer.delta.toFloat()
                if (animationTime < 0) {
                    animationTime += animationSize
                }
            }
            else -> Unit
        }
    }

    fun animate(animation: IAnimation, obj: IObjectRef, shader: UniversalShader) {
        val validOperations = animation.operations.values.filter { op ->
            op.startTime <= animationTime && op.endTime >= animationTime && obj in op.objects
        }

        if (validOperations.isNotEmpty()) {
            applyOperations(validOperations, animationTime, shader)
        }
    }

    private fun applyOperations(operations: List<IOperation>, time: Float, shader: UniversalShader) {

        operations.reduceAll(TRSTransformation.IDENTITY) { acc, op ->
            val step = ((time - op.startTime) / (op.endTime - op.startTime)).toDouble()
            val description = op.description

            when (description) {
                is ITranslationDescription -> acc.merge(TRSTransformation(translation = description.translation * step))
                is IRotationDescription -> acc.merge(TRSTransformation(rotation = description.rotation.lerp(step)))
                is IScaleDescription -> {
                    val diff = Vector3.ONE - description.scale
                    acc.merge(TRSTransformation(scale = Vector3.ONE + diff * step))
                }
                else -> acc
            }

        }.let { trans -> shader.matrixM.setMatrix4(trans.matrix) }
    }
}