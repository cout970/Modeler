package com.cout970.modeler.render.tool

import com.cout970.matrix.extensions.Matrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.animation.IOperation
import com.cout970.modeler.api.animation.ITranslationOperation
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.render.tool.shader.UniversalShader
import com.cout970.vector.extensions.times

object Animator {


    fun animate(animation: IAnimation, time: Float, obj: IObjectRef, shader: UniversalShader) {
        val validOperations = animation.operations.values.filter { it.startTime <= time && it.endTime >= time && obj in it.objects }

        if (validOperations.isNotEmpty()) {
            applyOperations(validOperations, time, shader)
        }
    }

    fun applyOperations(operations: List<IOperation>, time: Float, shader: UniversalShader) {
        var matrix = Matrix4.IDENTITY
        operations.map {
            val step = ((time - it.startTime) / (it.endTime - it.startTime)).toDouble()
            when (it) {
                is ITranslationOperation -> matrix *= TRSTransformation(translation = it.translation * step).matrix
                else -> {
                }
            }
        }
        shader.matrixM.setMatrix4(matrix)
    }
}