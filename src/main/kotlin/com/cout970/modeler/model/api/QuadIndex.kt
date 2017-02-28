package com.cout970.modeler.model.api

import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.Vertex

data class QuadIndex(val a: Pair<Int, Int>, val b: Pair<Int, Int>, val c: Pair<Int, Int>, val d: Pair<Int, Int>) {

    val pos: List<Int> get() = listOf(a, b, c, d).map { it.first }
    val tex: List<Int> get() = listOf(a, b, c, d).map { it.second }

    fun toQuad(obj: IElementLeaf): Quad {
        return Quad(
                Vertex(obj.positions[a.first], obj.textures[a.second]),
                Vertex(obj.positions[b.first], obj.textures[b.second]),
                Vertex(obj.positions[c.first], obj.textures[c.second]),
                Vertex(obj.positions[d.first], obj.textures[d.second])
        )
    }

    val edges: List<EdgeIndex> get() = listOf(
            EdgeIndex(a, b), EdgeIndex(b, c), EdgeIndex(c, d), EdgeIndex(d, a)
    )
}