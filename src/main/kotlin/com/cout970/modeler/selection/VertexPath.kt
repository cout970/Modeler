package com.cout970.modeler.selection

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Vertex
import com.cout970.modeler.model.util.getElement

data class VertexPath(val elementPath: ElementPath, val vertexIndex: Int) {

    fun toVertex(model: Model): Vertex {
        val elem = model.getElement(elementPath)
        //TODO remove, this is incorrect and can lead to an inconsistent program state
        return elem.getVertices()[vertexIndex]
    }
}