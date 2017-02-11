package com.cout970.modeler.model

import java.util.*

/**
 * Created by cout970 on 2017/02/11.
 */

open class ElementPath(open val indices: IntArray) {

    open fun getParent(): ElementPath? {
        if (indices.isEmpty()) return null
        return ElementPath(indices.take(indices.size - 1).toIntArray())
    }

    open fun getSubPaths(model: Model): List<ElementPath> {
        val elem = model.getElement(this)
        if (elem is IElementGroup) {
            return (0 until elem.elements.size).map { ElementPath(indices + it) }
        } else if (elem is IElementObject) {
            return elem.vertex.mapIndexed { i, vertexIndex ->
                VertexPath(indices, i)
            }
        }
        return listOf()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ElementPath) return false

        if (!Arrays.equals(indices, other.indices)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(indices)
    }
}

data class VertexPath(override val indices: IntArray, val vertexIndex: Int) : ElementPath(indices) {

    override fun getParent(): ElementPath? {
        return ElementPath(indices)
    }

    override fun getSubPaths(model: Model): List<ElementPath> {
        return listOf()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VertexPath) return false
        if (!super.equals(other)) return false

        if (!Arrays.equals(indices, other.indices)) return false
        if (vertexIndex != other.vertexIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + Arrays.hashCode(indices)
        result = 31 * result + vertexIndex
        return result
    }
}