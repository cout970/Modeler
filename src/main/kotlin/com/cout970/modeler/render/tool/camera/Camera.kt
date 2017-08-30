package com.cout970.modeler.render.tool.camera

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.MatrixUtils
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.util.toRads
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import org.joml.Matrix4d

/**
 * Created by cout970 on 2016/12/06.
 */
data class Camera(
        val position: IVector3,
        val angleX: Double,
        val angleY: Double,
        val zoom: Double,
        val perspective: Boolean
) {

    companion object {
        val DEFAULT = Camera(Vector3.ORIGIN, 45.0.toRads(), -45.0.toRads(), 64.0, true)
    }

    fun getMatrix(viewport: IVector2): IMatrix4 {
        val projection: IMatrix4
        val view: IMatrix4

        if (perspective) {
            projection = Matrix4d()
                    .setPerspective(Config.perspectiveFov.toRads(), viewport.xd / viewport.yd, 0.1, 1000.0)
                    .toIMatrix()

            view = matrixForPerspective
        } else {
            projection = MatrixUtils.createOrthoMatrix(viewport)
            view = matrixForOrtho
        }
        return projection * view
    }

    val matrixForPerspective by lazy {
        Matrix4d().apply {
            translate(0.0, 0.0, -zoom)
            rotate(angleX, 1.0, 0.0, 0.0)
            rotate(angleY, 0.0, 1.0, 0.0)
            scale(0.5)
            translate(position.xd, position.yd, position.zd)
        }.toIMatrix()
    }

    val matrixForOrtho by lazy {
        Matrix4d().apply {
            translate(0.0, 0.0, -32.0)
            rotate(angleX, 1.0, 0.0, 0.0)
            rotate(angleY, 0.0, 1.0, 0.0)
            scale(1 / zoom)
            translate(position.xd, position.yd, position.zd)
        }.toIMatrix()
    }

    val matrixForUV by lazy {
        Matrix4d().apply {
            translate(0.0, 0.0, -1.0)
            scale(1 / zoom)
            translate(position.xd, position.yd, 0.0)
        }.toIMatrix()
    }
}