package com.cout970.modeler.selection

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.util.getVertexPos
import com.cout970.modeler.model.util.middle
import com.cout970.vector.api.IVector3

class VertexPosSelection(val paths: List<VertexPosPaths>) {

    val pathList: List<VertexPath> get() = paths.flatMap { it.subPaths }.distinct()

    companion object {
        val EMPTY = VertexPosSelection(listOf())

        fun of(paths: List<VertexPath>): VertexPosSelection {
            val groupBy = paths.groupBy { it.elementPath }
            val vertexPosPaths = groupBy.map { VertexPosPaths(it.key, it.value) }
            return VertexPosSelection(vertexPosPaths)
        }
    }

    data class VertexPosPaths(
            val elementPath: ElementPath,
            val subPaths: List<VertexPath>
    )

    fun isSelected(path: VertexPath): Boolean {
        val pathsToElement = paths.find { it.elementPath == path.elementPath } ?: return false
        return pathsToElement.subPaths.any { it == path }
    }

    fun center3D(model: Model): IVector3 {
        return paths.flatMap {
            it.subPaths.map {
                model.getVertexPos(it)
            }
        }.middle()
    }

    fun toElementSelection(): ElementSelection {
        return ElementSelection(paths.map { it.elementPath })
    }
}