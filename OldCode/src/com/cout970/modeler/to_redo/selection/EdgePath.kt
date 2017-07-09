package com.cout970.modeler.to_redo.selection

import com.cout970.modeler.to_redo.model.Model
import com.cout970.modeler.to_redo.model.api.IElementLeaf
import com.cout970.modeler.to_redo.model.util.getElement
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/03/05.
 */
data class EdgePath(val elementPath: ElementPath, val firstIndex: Int, val secondIndex: Int) {

    fun toEdgePos(model: Model): Pair<IVector3, IVector3> {
        val elem = model.getElement(elementPath) as IElementLeaf
        return elem.positions[firstIndex] to elem.positions[secondIndex]
    }
}