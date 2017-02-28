package com.cout970.modeler.model.util

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.api.IElement
import com.cout970.modeler.model.api.IElementGroup
import com.cout970.modeler.model.api.IElementLeaf
import com.cout970.modeler.selection.*
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/02/27.
 */

fun Model.applyElementLeaves(selection: ElementSelection, func: (ElementPath, IElementLeaf) -> IElement?): Model {
    return copy(elements = elements.mapIndexedNotNull { i, element ->
        val path = ElementPath(intArrayOf(i))
        if (selection.containsSelectedElements(path)) {
            element.applyElementLeaves(path, selection, func)
        } else {
            element
        }
    })
}

fun IElement.applyElementLeaves(path: ElementPath, selection: ElementSelection,
                                func: (ElementPath, IElementLeaf) -> IElement?): IElement? {
    if (this is IElementGroup) {
        return deepCopy(elements.mapIndexedNotNull { i, element ->
            val subPath = ElementPath(path.indices + i)
            if (selection.containsSelectedElements(path)) {
                element.applyElementLeaves(subPath, selection, func)
            } else {
                element
            }
        })
    } else if (this is IElementLeaf) {
        return func(path, this)
    } else {
        throw IllegalStateException("Class ${this.javaClass} is not IElementGroup nor IElementLeaf")
    }
}

fun Model.applyVertexPos(selection: VertexPosSelection, func: (VertexPath, IVector3) -> IVector3): Model {
    val elemSel = selection.toElementSelection()
    return this.applyElementLeaves(elemSel) { elemPath, elem ->
        val paths = selection.paths.find { it.elementPath == elemPath } ?: throw IllegalStateException("")
        elem.transformPos(paths, func)
    }
}

fun Model.applyVertexTex(selection: VertexTexSelection, func: (VertexPath, IVector2) -> IVector2): Model {
    val elemSel = selection.toElementSelection()
    return this.applyElementLeaves(elemSel) { elemPath, elem ->
        val paths = selection.paths.find { it.elementPath == elemPath } ?: throw IllegalStateException("")
        elem.transformTex(paths, func)
    }
}
