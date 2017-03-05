package com.cout970.modeler.selection.subselection

import com.cout970.modeler.selection.EdgePath
import com.cout970.modeler.selection.FacePath
import com.cout970.modeler.selection.VertexPath

/**
 * Created by cout970 on 2017/03/05.
 */
data class SubSelectionVertex(val paths: List<VertexPath>) : ISubSelection {

    override val pathList: List<VertexPath> get() = paths

    override fun isSelected(path: VertexPath): Boolean = path in paths
    override fun isSelected(path: EdgePath): Boolean = false
    override fun isSelected(path: FacePath): Boolean = false
}