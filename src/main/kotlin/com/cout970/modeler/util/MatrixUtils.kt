package com.cout970.modeler.util

import com.cout970.matrix.api.IMatrix4
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.xd
import com.cout970.vector.extensions.yd
import org.joml.Matrix4d

/**
 * Created by cout970 on 2017/03/26.
 */

object MatrixUtils {

    fun createOrthoMatrix(size: IVector2): IMatrix4 {
        val aspectRatio = (size.yd / size.xd)
        return Matrix4d().setOrtho(-1.0 / aspectRatio, 1.0 / aspectRatio, -1.0, 1.0, 0.1, 10000.0).toIMatrix()
    }
}