package com.cout970.modeler.core.collision

import com.cout970.collision.IPolygon
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of

data class TexturePolygon(private val vertex: List<IVector2>) : IPolygon {

    override fun getEdges(): List<Pair<IVector2, IVector2>> = if (vertex.size == 3) {
        listOf(
            vertex[0] to vertex[2],
            vertex[2] to vertex[1],
            vertex[1] to vertex[0]
        )
    } else {
        listOf(
            vertex[0] to vertex[1],
            vertex[1] to vertex[2],
            vertex[2] to vertex[3],
            vertex[3] to vertex[0]
        )
    }

    override fun getVertex(): List<IVector2> = vertex
}

fun Pair<IVector2, IVector2>.toTexturePolygon(): TexturePolygon {
    val a = this.first
    val b = this.second

    val points = listOf(
        vec2Of(a.x, a.y),
        vec2Of(b.x, a.y),
        vec2Of(b.x, b.y),
        vec2Of(a.x, b.y)
    )

    return TexturePolygon(points)
}