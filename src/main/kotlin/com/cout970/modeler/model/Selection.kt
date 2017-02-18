package com.cout970.modeler.model

import com.cout970.modeler.model.Quad
import com.cout970.modeler.modeleditor.SelectionMode
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/02/11.
 */

open class Selection(open val paths: List<ElementPath>) {

    open val mode = SelectionMode.ELEMENT

    fun isSelected(path: ElementPath): Boolean {
        return paths.any { it == path }
    }

    fun containsSelectedElements(path: ElementPath): Boolean {
        return paths.any { item ->
            path.indices.none { item.indices[it] != path.indices[it] }
        }
    }

    fun filterPaths(path: ElementPath): List<ElementPath> {
        //TODO java.lang.ArrayIndexOutOfBoundsException: 1
        return paths.filter { item ->
            path.indices.none { item.indices[it] != path.indices[it] }
        }
    }

    fun center3D(model: Model): IVector3 {
        return paths.map {
            if (it is VertexPath) {
                model.getVertex(it).pos
            } else {
                model.getQuads(it).map(Quad::center3D).middle()
            }
        }.middle()
    }

    fun center2D(model: Model): IVector2 {
        return paths.map {
            if (it is VertexPath) {
                model.getVertex(it).tex
            } else {
                model.getQuads(it).map(Quad::center2D).middle()
            }
        }.middle()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Selection) return false

        if (paths != other.paths) return false

        return true
    }

    override fun hashCode(): Int {
        return paths.hashCode()
    }
}
object SelectionNone : Selection(listOf())


class VertexSelection(override val paths: List<VertexPath>) : Selection(paths) {
    override val mode = SelectionMode.EDIT
}