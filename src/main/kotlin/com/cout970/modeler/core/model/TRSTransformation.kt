package com.cout970.modeler.core.model

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.util.toJOML
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.xd
import com.cout970.vector.extensions.yd
import com.cout970.vector.extensions.zd
import org.joml.Matrix4d

/**
 * Created by cout970 on 2017/05/14.
 */
class TRSTransformation(
        val translation: IVector3,
        val rotation: IQuaternion,
        val scale: IVector3
) : ITransformation {

    override val matrix: IMatrix4 by lazy {
        Matrix4d().run {
            translate(translation.xd, translation.yd, translation.zd)
            rotate(rotation.toJOML())
            scale(scale.xd, scale.yd, scale.zd)
            toIMatrix()
        }
    }
}