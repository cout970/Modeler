package com.cout970.modeler.model.structure

import com.cout970.modeler.model.Edge
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.util.getVertex
import com.cout970.modeler.selection.VertexPath

data class VertexStructurePath(
        val quads: List<QuadPath>,
        val edges: List<EdgePath>,
        val vertex: List<Pair<VertexPath, VertexPath>>
) {

    fun toStructure(model: Model): VertexStructure {
        return VertexStructure(
                quads.map { it.toQuad(model) },
                edges.map { it.toEdge(model) },
                vertex.map { model.getVertex(it.first, it.second) }
        )
    }

    data class EdgePath(val a: Pair<VertexPath, VertexPath>, val b: Pair<VertexPath, VertexPath>) {
        fun toEdge(model: Model): Edge {
            return Edge(
                    model.getVertex(a.first, a.second),
                    model.getVertex(b.first, b.second)
            )
        }
    }

    data class QuadPath(val vertex: List<Pair<VertexPath, VertexPath>>) {
        fun toQuad(model: Model): Quad {
            return Quad(
                    model.getVertex(vertex[0].first, vertex[0].second),
                    model.getVertex(vertex[1].first, vertex[1].second),
                    model.getVertex(vertex[2].first, vertex[2].second),
                    model.getVertex(vertex[3].first, vertex[3].second)
            )
        }
    }
}