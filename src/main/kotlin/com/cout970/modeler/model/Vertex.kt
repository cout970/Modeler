package com.cout970.modeler.model

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.times
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.toVector4

/**
 * Created by cout970 on 2016/12/04.
 */
data class Vertex(
        val pos: IVector3,
        val tex: IVector2
) {
    fun transform(matrix: IMatrix4): Vertex {
        return Vertex(matrix * pos.toVector4(1.0), tex)
    }

    fun transformPos(func: (IVector3) -> IVector3): Vertex {
        return Vertex(func(pos), tex)
    }

    fun transformTex(func: (IVector2) -> IVector2): Vertex {
        return Vertex(pos, func(tex))
    }
}