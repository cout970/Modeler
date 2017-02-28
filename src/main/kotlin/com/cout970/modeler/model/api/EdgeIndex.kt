package com.cout970.modeler.model.api

import com.cout970.modeler.model.Edge
import com.cout970.modeler.model.Vertex

/**
 * Created by cout970 on 2017/02/27.
 */
data class EdgeIndex(val a: Pair<Int, Int>, val b: Pair<Int, Int>) {

    val pos: List<Int> get() = listOf(a, b).map { it.first }
    val tex: List<Int> get() = listOf(a, b).map { it.second }

    fun toEdge(obj: IElementLeaf): Edge {
        return Edge(
                Vertex(obj.positions[a.first], obj.textures[a.second]),
                Vertex(obj.positions[b.first], obj.textures[b.second])
        )
    }
}