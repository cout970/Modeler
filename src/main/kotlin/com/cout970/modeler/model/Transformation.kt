package com.cout970.modeler.model

import com.cout970.matrix.extensions.asImmutable
import com.cout970.matrix.extensions.mutableMat4Of
import com.cout970.matrix.extensions.rotate
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2016/11/29.
 */
data class Transformation(
        val position: IVector3,
        val rotation: IQuaternion,
        val scale: IVector3
) {
    val matrix by lazy {
        mutableMat4Of(
                scale.x, 0, 0, position.x,
                0, scale.y, 0, position.y,
                0, 0, scale.z, position.z,
                0, 0, 0, 1
        ).apply { rotate(rotation) }.asImmutable()
    }
}