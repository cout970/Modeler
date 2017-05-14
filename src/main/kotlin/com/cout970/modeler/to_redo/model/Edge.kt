package com.cout970.modeler.to_redo.model

import com.cout970.modeler.to_redo.model.api.IVertexCompound

/**
 * Created by cout970 on 2017/02/16.
 */
data class Edge(
        val a: Vertex,
        val b: Vertex
) : IVertexCompound {

    override val vertex: List<Vertex> get() = listOf(a, b)
}