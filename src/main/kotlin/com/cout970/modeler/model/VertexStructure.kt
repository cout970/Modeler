package com.cout970.modeler.model

/**
 * Created by cout970 on 2017/02/16.
 */
data class VertexStructure(
        val quads: List<Quad>,
        val edges: List<Edge>,
        val vertex: List<Vertex>
)

data class VertexStructurePath(
        val quads: List<List<VertexPath>>,
        val edges: List<Pair<VertexPath, VertexPath>>,
        val vertex: List<VertexPath>
) {
    fun toStructure(model: Model): VertexStructure {
        return VertexStructure(
                quads.map {
                    Quad(
                            model.getVertex(it[0]),
                            model.getVertex(it[1]),
                            model.getVertex(it[2]),
                            model.getVertex(it[3])
                    )
                },
                edges.map {
                    Edge(
                            model.getVertex(it.first),
                            model.getVertex(it.second)
                    )
                },
                vertex.map {
                    model.getVertex(it)
                }
        )
    }
}