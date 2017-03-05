package com.cout970.modeler.selection

import com.cout970.modeler.selection.subselection.ISubSelection
import com.cout970.modeler.selection.subselection.SubSelectionEdge
import com.cout970.modeler.selection.subselection.SubSelectionFace
import com.cout970.modeler.selection.subselection.SubSelectionVertex

data class VertexTexSelection(val subPathHandler: ISubSelection) : ISubSelection by subPathHandler {

    companion object {
        val EMPTY = VertexTexSelection(SubSelectionVertex(listOf()))

        fun ofVertex(paths: List<VertexPath>): VertexTexSelection {
            return VertexTexSelection(SubSelectionVertex(paths))
        }

        fun ofEdges(paths: List<EdgePath>): VertexTexSelection {
            return VertexTexSelection(SubSelectionEdge(paths))
        }

        fun ofFaces(paths: List<FacePath>): VertexTexSelection {
            return VertexTexSelection(SubSelectionFace(paths))
        }
    }
}