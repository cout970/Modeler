package com.cout970.modeler.selection

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.api.IElementLeaf
import com.cout970.modeler.model.util.getElement

/**
 * Created by cout970 on 2017/02/11.
 */

data class ElementSelection(
        val paths: List<ElementPath>
) {

    companion object {
        val EMPTY = ElementSelection(listOf())
    }

    fun isSelected(path: ElementPath): Boolean {
        return paths.any { it == path }
    }

    fun containsSelectedElements(path: ElementPath): Boolean {
        //TODO fixme
        // java.lang.ArrayIndexOutOfBoundsException: 1
        // at com.cout970.modeler.selection.ElementSelection.containsSelectedElements(ElementSelection.kt:21)
        return paths.any { item ->
            path.indices.none { item.indices.getOrNull(it) != path.indices.getOrNull(it) }
        }
    }

    fun filter(path: ElementPath): List<ElementPath> {
        return paths.filter { item ->
            path.indices.none { item.indices[it] != path.indices[it] }
        }
    }

    fun getSelectedVertexPos(model: Model): VertexPosSelection {
        return VertexPosSelection.ofVertex(
                paths.map { it to model.getElement(it) as IElementLeaf }
                        .flatMap { (path, elem) ->
                            (0 until elem.positions.size).map { VertexPath(path, it) }
                        }
        )
    }
}

