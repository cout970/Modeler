package com.cout970.modeler.core.model

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.util.toJOML
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Quaternion
import com.cout970.vector.extensions.Vector3
import org.joml.Matrix4d

/**
 * Created by cout970 on 2017/05/14.
 */
data class TRSTransformation(
        val translation: IVector3 = Vector3.ORIGIN,
        val rotation: IQuaternion = Quaternion.IDENTITY,
        val scale: IVector3 = Vector3.ONE
) : ITransformation {

    companion object {
        val IDENTITY = TRSTransformation(Vector3.ORIGIN, Quaternion.IDENTITY, Vector3.ONE)
    }

    // Gson pls
    private constructor() : this(Vector3.ORIGIN, Quaternion.IDENTITY, Vector3.ONE)

    override val matrix: IMatrix4 by lazy {
        Matrix4d().apply {
            translate(translation.xd, translation.yd, translation.zd)
            rotate(rotation.toJOML())
            scale(scale.xd, scale.yd, scale.zd)
        }.toIMatrix()
    }
}