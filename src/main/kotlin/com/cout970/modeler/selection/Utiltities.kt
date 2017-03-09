package com.cout970.modeler.selection

import com.cout970.modeler.modeleditor.SelectionManager
import com.cout970.modeler.selection.subselection.ISubSelection
import com.cout970.modeler.selection.subselection.SubSelectionEdge
import com.cout970.modeler.selection.subselection.SubSelectionFace

/**
 * Created by cout970 on 2017/03/05.
 */

fun ISubSelection.toEdgePaths(): List<EdgePath> {
    if (this is VertexPosSelection) return subPathHandler.toEdgePaths()
    if (this is VertexTexSelection) return subPathHandler.toEdgePaths()
    if (this is SubSelectionEdge) return paths
    return listOf()
}

fun ISubSelection.toFacePaths(): List<FacePath> {
    if (this is VertexPosSelection) return subPathHandler.toFacePaths()
    if (this is VertexTexSelection) return subPathHandler.toFacePaths()
    if (this is SubSelectionFace) return paths
    return listOf()
}

val SelectionManager.elementSelection: ElementSelection get() = selectionState.element
val SelectionManager.vertexPosSelection: VertexPosSelection get() = selectionState.pos
val SelectionManager.vertexTexSelection: VertexTexSelection get() = selectionState.tex