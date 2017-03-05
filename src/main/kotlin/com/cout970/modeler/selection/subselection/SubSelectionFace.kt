package com.cout970.modeler.selection.subselection

import com.cout970.modeler.selection.EdgePath
import com.cout970.modeler.selection.FacePath
import com.cout970.modeler.selection.VertexPath

data class SubSelectionFace(val paths: List<FacePath>) : ISubSelection {

    override val pathList: List<VertexPath> = paths.flatMap { face ->
        face.vertex.map { VertexPath(face.elementPath, it) }
    }

    override fun isSelected(path: VertexPath): Boolean = false
    override fun isSelected(path: EdgePath): Boolean = false
    override fun isSelected(path: FacePath): Boolean = path in paths
}