package com.cout970.modeler.to_redo.selection

import com.cout970.modeler.to_redo.model.Model
import com.cout970.modeler.to_redo.model.api.IElementGroup
import com.cout970.modeler.to_redo.model.api.IElementLeaf
import com.cout970.modeler.to_redo.model.util.getElement
import java.util.*

/**
 * Created by cout970 on 2017/02/11.
 */

data class ElementPath(val indices: IntArray) {

    fun getParent(): ElementPath? {
        if (indices.isEmpty()) return null
        return ElementPath(indices.take(indices.size - 1).toIntArray())
    }

    fun getSubPaths(model: Model): List<ElementPath> {
        val elem = model.getElement(this)
        if (elem is IElementGroup) {
            return (0 until elem.elements.size).map { ElementPath(indices + it) }
        }
        return listOf()
    }

    fun isLeaf(model: Model) = model.getElement(this) is IElementLeaf

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