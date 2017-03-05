package com.cout970.modeler.selection.subselection

import com.cout970.modeler.selection.EdgePath
import com.cout970.modeler.selection.FacePath
import com.cout970.modeler.selection.VertexPath

data class SubSelectionEdge(val paths: List<EdgePath>) : ISubSelection {

    override val pathList: List<VertexPath> = paths.flatMap {
        listOf(VertexPath(it.elementPath, it.firstIndex), VertexPath(it.elementPath, it.secondIndex))
    }.distinct()

    override fun isSelected(path: VertexPath): Boolean = false
    override fun isSelected(path: EdgePath): Boolean = path in paths
    override fun isSelected(path: FacePath): Boolean = false
}