package com.cout970.modeler.model.api

import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.Vertex

data class QuadIndex(val a: VertexIndex, val b: VertexIndex, val c: VertexIndex, val d: VertexIndex) {

    val pos: List<Int> get() = listOf(a, b, c, d).map { it.pos }
    val tex: List<Int> get() = listOf(a, b, c, d).map { it.tex }

    fun toQuad(obj: IElementLeaf): Quad {
        return Quad(
                Vertex(obj.positions[a.pos], obj.textures[a.tex]),
                Vertex(obj.positions[b.pos], obj.textures[b.tex]),
                Vertex(obj.positions[c.pos], obj.textures[c.tex]),
                Vertex(obj.positions[d.pos], obj.textures[d.tex])
        )
    }

    val edges: List<EdgeIndex> get() = listOf(
            EdgeIndex(a, b), EdgeIndex(b, c), EdgeIndex(c, d), EdgeIndex(d, a)
    )
}