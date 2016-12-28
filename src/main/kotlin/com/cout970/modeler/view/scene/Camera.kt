package com.cout970.modeler.view.scene

import com.cout970.modeler.util.toIMatrix
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.xd
import com.cout970.vector.extensions.yd
import com.cout970.vector.extensions.zd
import org.joml.Matrix4d

/**
 * Created by cout970 on 2016/12/06.
 */
data class Camera(
        val position: IVector3,
        val angleX: Double,
        val angleY: Double,
        val zoom: Double
) {

    companion object {
        val DEFAULT = Camera(Vector3.ORIGIN, 0.0, 0.0, 32.0)
    }

    val matrix by lazy {
        Matrix4d().apply {
            translate(0.0, 0.0, -zoom)
            rotate(angleX, 1.0, 0.0, 0.0)
            rotate(angleY, 0.0, 1.0, 0.0)
            translate(position.xd, position.yd, position.zd)
        }.toIMatrix()
    }
}