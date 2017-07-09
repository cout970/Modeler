package com.cout970.modeler.to_redo.selection

import com.cout970.modeler.to_redo.selection.subselection.ISubSelection
import com.cout970.modeler.to_redo.selection.subselection.SubSelectionEdge
import com.cout970.modeler.to_redo.selection.subselection.SubSelectionFace
import com.cout970.modeler.to_redo.selection.subselection.SubSelectionVertex

data class VertexPosSelection(val subPathHandler: ISubSelection) : ISubSelection by subPathHandler {

    companion object {
        val EMPTY = VertexPosSelection(SubSelectionVertex(listOf()))

        fun ofVertex(paths: List<VertexPath>): VertexPosSelection {
            return VertexPosSelection(SubSelectionVertex(paths))
        }

        fun ofEdges(paths: List<EdgePath>): VertexPosSelection {
            return VertexPosSelection(SubSelectionEdge(paths))
        }

        fun ofFaces(paths: List<FacePath>): VertexPosSelection {
            return VertexPosSelection(SubSelectionFace(paths))
        }
    }

}