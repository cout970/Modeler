package com.cout970.modeler.model.freemodel

import com.cout970.modeler.model.Quad
import com.cout970.modeler.util.center2D
import com.cout970.modeler.util.center3D
import com.cout970.modeler.util.middle
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/02/11.
 */

open class Selection(open val paths: List<ElementPath>) {

    fun isSelected(path: ElementPath): Boolean {
        return paths.any { it == path }
    }

    fun containsSelectedElements(path: ElementPath): Boolean {
        return paths.any { item ->
            path.indices.none { item.indices[it] != path.indices[it] }
        }
    }

    fun filterPaths(path: ElementPath): List<ElementPath> {
        return paths.filter { item ->
            path.indices.none { item.indices[it] != path.indices[it] }
        }
    }

    fun center3D(model: FreeModel): IVector3 {
        return paths.map {
            if (it is VertexPath) {
                model.getVertex(it).pos
            } else {
                model.getQuads(it).map(Quad::center3D).middle()
            }
        }.middle()
    }

    fun center2D(model: FreeModel): IVector2 {
        return paths.map {
            if (it is VertexPath) {
                model.getVertex(it).tex
            } else {
                model.getQuads(it).map(Quad::center2D).middle()
            }
        }.middle()
    }
}

class VertexSelection(override val paths: List<VertexPath>) : Selection(paths)
