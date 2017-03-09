package com.cout970.modeler.selection

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Vertex
import com.cout970.modeler.model.util.getElement

data class VertexPath(val elementPath: ElementPath, val vertexIndex: Int) {

    fun toVertex(model: Model): Vertex {
        val elem = model.getElement(elementPath)
        return elem.getVertices()[vertexIndex]
    }
}