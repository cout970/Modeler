package com.cout970.modeler.model

import com.cout970.matrix.extensions.asImmutable
import com.cout970.matrix.extensions.mutableMat4Of
import com.cout970.matrix.extensions.rotate
import com.cout970.matrix.extensions.times
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Quaternion
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.toVector4

/**
 * Created by cout970 on 2016/11/29.
 */
data class Transformation(
        val position: IVector3,
        val rotation: IQuaternion,
        val scale: IVector3
) {

    companion object {
        val IDENTITY = Transformation(Vector3.ORIGIN, Quaternion.IDENTITY, Vector3.ONE)
    }

    val matrix by lazy {
        mutableMat4Of(
                scale.x, 0, 0, position.x,
                0, scale.y, 0, position.y,
                0, 0, scale.z, position.z,
                0, 0, 0, 1
        ).apply { rotate(rotation) }.asImmutable()
    }

    fun apply(v: Vertex): Vertex {
        return Vertex(matrix * v.pos.toVector4(1.0), v.tex)
    }
}