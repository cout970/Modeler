package com.cout970.modeler.selection

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.util.getVertexTex
import com.cout970.modeler.model.util.middle
import com.cout970.vector.api.IVector2

data class VertexTexSelection(val paths: List<VertexTexPaths>) {

    val pathList: List<VertexPath> get() = paths.flatMap { it.subPaths }.distinct()

    companion object {
        val EMPTY = VertexTexSelection(listOf())

        fun of(paths: List<VertexPath>): VertexTexSelection {
            val groupBy = paths.groupBy { it.elementPath }
            val vertexTexPaths = groupBy.map { VertexTexPaths(it.key, it.value) }
            return VertexTexSelection(vertexTexPaths)
        }
    }

    data class VertexTexPaths(
            val elementPath: ElementPath,
            val subPaths: List<VertexPath>
    )

    fun isSelected(path: VertexPath): Boolean {
        val pathsToElement = paths.find { it.elementPath == path.elementPath } ?: return false
        return pathsToElement.subPaths.any { it == path }
    }

    fun center2D(model: Model): IVector2 {
        return paths.flatMap {
            it.subPaths.map {
                model.getVertexTex(it)
            }
        }.middle()
    }

    fun toElementSelection(): ElementSelection {
        return ElementSelection(paths.map { it.elementPath })
    }
}