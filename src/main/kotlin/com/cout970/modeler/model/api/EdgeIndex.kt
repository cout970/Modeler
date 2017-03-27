package com.cout970.modeler.model.api

import com.cout970.modeler.model.Edge
import com.cout970.modeler.model.Vertex

/**
 * Created by cout970 on 2017/02/27.
 */
data class EdgeIndex(val a: VertexIndex, val b: VertexIndex) {

    val pos: List<Int> get() = listOf(a, b).map { it.pos }
    val tex: List<Int> get() = listOf(a, b).map { it.tex }

    fun toEdge(obj: IElementLeaf): Edge {
        return Edge(
                Vertex(obj.positions[a.pos], obj.textures[a.tex]),
                Vertex(obj.positions[b.pos], obj.textures[b.tex])
        )
    }
}