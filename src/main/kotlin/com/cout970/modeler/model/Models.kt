package com.cout970.modeler.model

import com.cout970.modeler.model.Quad
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/02/11.
 */

// the id is used to get a different hashCode for every model, so this can be used to detect changes
private var modelIds = 0

data class Model(val elements: List<IElement>, val resources: ModelResources, val id: Int = modelIds++) {

    //copies the model with a different modelId so the hashCode of the model is different
    fun copy(elements: List<IElement> = this.elements): Model {
        return Model(elements, resources)
    }

    fun getQuads(): List<Quad> = elements.flatMap { it.getQuads() }

    fun getVertices(): List<Vertex> = elements.flatMap { it.getVertices() }

    //TODO add quad deletion
    fun delete(selection: Selection): Model {
        val elements = mutableListOf<IElement>()

        for ((index, elem) in this.elements.withIndex()) {
            val path = ElementPath(intArrayOf(index))
            if (!selection.isSelected(path)) {
                if (elem is IElementGroup && selection.containsSelectedElements(path)) {
                    elements += elem.delete(selection, path)
                } else {
                    elements += elem
                }
            }
        }
        return copy(elements = elements)
    }

    private fun IElementGroup.delete(selection: Selection, path: ElementPath): IElementGroup {
        val elements = mutableListOf<IElement>()

        for ((index, elem) in this.elements.withIndex()) {
            val subPath = ElementPath(path.indices + index)
            if (!selection.isSelected(subPath)) {
                if (elem is IElementGroup && selection.containsSelectedElements(subPath)) {
                    elements += elem.delete(selection, subPath)
                } else {
                    elements += elem
                }
            }
        }
        return deepCopy(elements = elements)
    }
}

data class ModelResources(val materials: List<Material>)

private var groupCount = 0

data class ElementGroup(
        override val elements: List<IElement>,
        override val name: String = "Group_${groupCount++}"
) : IElementGroup {

    override fun getQuads(): List<Quad> = elements.flatMap { it.getQuads() }
    override fun getVertices(): List<Vertex> = elements.flatMap { it.getVertices() }

    override fun deepCopy(elements: List<IElement>): IElementGroup {
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

    fun transform(func: (Vertex) -> Vertex): ElementObject {
        return updateVertex(vertex.map { it.toVertex(this) }.map(func))
    }

    override fun updateVertex(newVertex: List<Vertex>): ElementObject {
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