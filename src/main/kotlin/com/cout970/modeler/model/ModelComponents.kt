package com.cout970.modeler.model

/**
 * Created by cout970 on 2016/11/29.
 */

sealed class ModelComponent() {

    abstract fun getQuads(): List<Quad>
    abstract fun getVertices(): List<Vertex>
}

data class Mesh(
        var vertex: List<Vertex>,
        var indices: List<QuadIndices>
) : ModelComponent() {

    override fun getQuads(): List<Quad> = indices.map { it.toQuad(vertex) }

    override fun getVertices(): List<Vertex> = vertex

    data class QuadIndices(val a: Int, val b: Int, val c: Int, val d: Int) {
        fun toQuad(vertex: List<Vertex>): Quad = Quad(vertex[a], vertex[b], vertex[c], vertex[d])
    }
}

data class Plane(
        var vertex0: Vertex,
        var vertex1: Vertex,
        var vertex2: Vertex,
        var vertex3: Vertex
) : ModelComponent() {

    override fun getQuads(): List<Quad> = listOf(Quad(vertex0, vertex1, vertex2, vertex3))

    override fun getVertices(): List<Vertex> = listOf(vertex0, vertex1, vertex2, vertex3)
}

data class Cube(
        var NNN: Vertex,
        var PNN: Vertex,
        var NPN: Vertex,
        var PPN: Vertex,
        var NNP: Vertex,
        var PNP: Vertex,
        var NPP: Vertex,
        var PPP: Vertex
) : ModelComponent() {

    override fun getQuads(): List<Quad> = listOf(
            Quad(NNN, NNP, NPN, NPP),
            Quad(PNN, PNP, PPN, PPP),
            Quad(NNN, NPN, PNN, PPN),
            Quad(NNP, NPP, PNP, PPP),
            Quad(NNN, NNP, PNN, PNP),
            Quad(NPN, NPP, PPN, PPP)
    )

    override fun getVertices(): List<Vertex> = listOf(NNN, PNN, NPN, PPN, NNP, PNP, NPP, PPP)
}