package com.cout970.modeler.to_redo.selection

import com.cout970.modeler.to_redo.model.Model
import com.cout970.modeler.to_redo.model.Vertex
import com.cout970.modeler.to_redo.model.util.getElement

data class VertexPath(val elementPath: ElementPath, val vertexIndex: Int) {

    fun toVertex(model: Model): Vertex {
        val elem = model.getElement(elementPath)
        //TODO remove, this is incorrect and can lead to an inconsistent program state
        return elem.getVertices()[vertexIndex]
    }
}