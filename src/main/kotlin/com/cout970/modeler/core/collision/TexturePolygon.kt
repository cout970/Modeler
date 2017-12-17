package com.cout970.modeler.core.collision

import com.cout970.collision.IPolygon
import com.cout970.vector.api.IVector2

data class TexturePolygon(private val vertex: List<IVector2>) : IPolygon {

    override fun getEdges(): List<Pair<IVector2, IVector2>> = listOf(
            vertex[0] to vertex[1],
            vertex[1] to vertex[2],
            vertex[2] to vertex[3],
            vertex[3] to vertex[0]
    )

    override fun getVertex(): List<IVector2> = vertex
}