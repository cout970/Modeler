package com.cout970.modeler.model

import com.cout970.matrix.api.IMatrix4
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.cross
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.normalize
import com.cout970.vector.extensions.vec2Of

/**
 * Created by cout970 on 2016/12/04.
 */
data class Quad(
        val a: Vertex,
        val b: Vertex,
        val c: Vertex,
        val d: Vertex
) {
    val vertex: List<Vertex> get() = listOf(a, b, c, d)

    val normal: IVector3 by lazy {
        val ab = b.pos - a.pos
        val ac = c.pos - a.pos
        (ab cross ac).normalize()
    }

    companion object {

        fun create(a: IVector3, b: IVector3, c: IVector3, d: IVector3): Quad {
            return Quad(
                    Vertex(a, vec2Of(0, 0)),
                    Vertex(b, vec2Of(1, 0)),
                    Vertex(c, vec2Of(1, 1)),
                    Vertex(d, vec2Of(0, 1)))
        }
    }

    fun transform(matrix: IMatrix4): Quad {
        return Quad(a.transform(matrix), b.transform(matrix), c.transform(matrix), d.transform(matrix))
    }
}