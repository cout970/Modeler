package com.cout970.modeler.model

import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.cross
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.normalize

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
}