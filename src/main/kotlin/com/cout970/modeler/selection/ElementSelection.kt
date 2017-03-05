package com.cout970.modeler.selection

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
}

