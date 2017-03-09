package com.cout970.modeler.selection

import com.cout970.modeler.model.Edge
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.util.getElement

/**
 * Created by cout970 on 2017/03/05.
 */
data class EdgePath(val elementPath: ElementPath, val firstIndex: Int, val secondIndex: Int) {
    fun toEdge(model: Model): Edge {
        val elem = model.getElement(elementPath)
        return Edge(
                elem.getVertices()[firstIndex],
                elem.getVertices()[secondIndex]
        )
    }
}