package com.cout970.modeler.to_redo.model

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.to_redo.model.api.IVertexCompound
import com.cout970.modeler.to_redo.model.api.VertexIndex
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.toVector4

/**
 * Created by cout970 on 2016/12/04.
 */
data class Vertex(
        val pos: IVector3,
        val tex: IVector2
) : IVertexCompound {
    override val vertex: List<Vertex> get() = listOf(this)

    fun transform(matrix: IMatrix4): Vertex {
        return Vertex(matrix * pos.toVector4(1.0), tex)
    }

    fun transformPos(func: (IVector3) -> IVector3): Vertex {
        return Vertex(func(pos), tex)
    }

    fun transformTex(func: (IVector2) -> IVector2): Vertex {
        return Vertex(pos, func(tex))
    }

    fun toIndex(pos: List<IVector3>, tex: List<IVector2>) = VertexIndex(pos.indexOf(this.pos), tex.indexOf(this.tex))
}