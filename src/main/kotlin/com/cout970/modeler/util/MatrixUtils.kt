package com.cout970.modeler.util

import com.cout970.matrix.api.IMatrix4
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec2Of
import org.joml.Matrix4d
import org.joml.Vector3d

/**
 * Created by cout970 on 2017/03/26.
 */

object MatrixUtils {

    fun createOrthoMatrix(size: IVector2): IMatrix4 {
        val aspectRatio = (size.yd / size.xd)
        return Matrix4d().setOrtho(-1.0 / aspectRatio, 1.0 / aspectRatio, -1.0, 1.0, 0.1, 10000.0).toIMatrix()
    }

    fun projectAxis(matrix: Matrix4d, dir: IVector3): Pair<IVector2, IVector2> {
        val start = matrix.project(Vector3.ORIGIN.toJoml3d(), intArrayOf(-1, -1, 2, 2), Vector3d())
        val end = matrix.project(dir.toJoml3d(), intArrayOf(-1, -1, 2, 2), Vector3d())

        return vec2Of(start.x, start.y) to vec2Of(end.x, end.y)
    }
}