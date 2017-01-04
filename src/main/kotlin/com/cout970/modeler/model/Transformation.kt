package com.cout970.modeler.model

import com.cout970.matrix.extensions.*
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import com.google.gson.annotations.Expose

/**
 * Created by cout970 on 2016/11/29.
 */
data class Transformation(
        @Expose val position: IVector3 = vec3Of(0),
        @Expose val rotation: IQuaternion = Quaternion.IDENTITY,
        @Expose val scale: IVector3 = vec3Of(1)
) {

    companion object {
        val IDENTITY = Transformation(Vector3.ORIGIN, Quaternion.IDENTITY, Vector3.ONE)
    }

    val matrix by lazy {
        mutableMat4Of(1).apply {
            translate(position)
            rotate(rotation)
            scale(scale)
        }.asImmutable().transpose()
    }

    fun apply(v: Vertex): Vertex {
        return Vertex(matrix.transpose() * v.pos.toVector4(1.0), v.tex)
    }

    fun move(axis: SelectionAxis, offset: Float): Transformation {
        val direction = axis.axis * offset
        return copy(position = position + direction)
    }
}