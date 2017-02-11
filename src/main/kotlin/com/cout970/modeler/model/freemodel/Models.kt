package com.cout970.modeler.model.freemodel

import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.Vertex
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/02/11.
 */

// the id is used to get a different hashCode for every model, so this can be used to detect changes
private var modelIds = 0

data class FreeModel(val elements: List<Element>, val id: Int = modelIds++) {

    //copies the model with a different modelId so the hashCode of the model is different
    fun copy(elements: List<Element> = this.elements): FreeModel {
        return FreeModel(elements)
    }

    fun getQuads(): List<Quad> = elements.flatMap { it.getQuads() }

    fun getVertices(): List<Vertex> = elements.flatMap { it.getVertices() }
}

interface Element {

    fun getQuads(): List<Quad>
    fun getVertices(): List<Vertex>
}

interface IElementGroup : Element {

    val elements: List<Element>

    fun deepCopy(elements: List<Element>): IElementGroup
}

interface IElementObject : Element {

    val positions: List<IVector3>
    val textures: List<IVector2>
    val vertex: List<VertexIndex>
    val faces: List<QuadIndex>

    fun updateVertex(newVertex: List<Vertex>): IElementObject
}

data class ElementGroup(override val elements: List<Element>) : IElementGroup {

    override fun getQuads(): List<Quad> = elements.flatMap { it.getQuads() }
    override fun getVertices(): List<Vertex> = elements.flatMap { it.getVertices() }

    override fun deepCopy(elements: List<Element>): IElementGroup {
        return ElementGroup(elements)
    }
}

data class ElementObject(
        override val positions: List<IVector3>,
        override val textures: List<IVector2>,
        override val vertex: List<VertexIndex>,
        override val faces: List<QuadIndex>
) : IElementObject {

    override fun getQuads(): List<Quad> = faces.map { (a, b, c, d) ->
        Quad(
                Vertex(positions[vertex[a].pos], textures[vertex[a].tex]),
                Vertex(positions[vertex[b].pos], textures[vertex[b].tex]),
                Vertex(positions[vertex[c].pos], textures[vertex[c].tex]),
                Vertex(positions[vertex[d].pos], textures[vertex[d].tex])
        )
    }

    override fun updateVertex(newVertex: List<Vertex>): IElementObject {
        val positions = newVertex.map { it.pos }.distinct()
        val textures = newVertex.map { it.tex }.distinct()

        val vertex = newVertex.map {
            VertexIndex(positions.indexOf(it.pos), textures.indexOf(it.tex))
        }

        return ElementObject(positions, textures, vertex, faces)
    }

    override fun getVertices(): List<Vertex> = getQuads().flatMap(Quad::vertex).distinct()
}

data class VertexIndex(val pos: Int, val tex: Int) {
    fun toVertex(obj: IElementObject): Vertex {
        return Vertex(obj.positions[pos], obj.textures[tex])
    }
}

data class QuadIndex(val a: Int, val b: Int, val c: Int, val d: Int) {
    val indices: List<Int> get() = listOf(a, b, c, d)

    fun toQuad(obj: IElementObject): Quad {
        return Quad(
                obj.vertex[a].toVertex(obj),
                obj.vertex[b].toVertex(obj),
                obj.vertex[c].toVertex(obj),
                obj.vertex[d].toVertex(obj)
        )
    }
}