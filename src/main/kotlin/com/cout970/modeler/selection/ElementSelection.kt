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
        return paths.any { item ->
            path.indices.none { item.indices[it] != path.indices[it] }
        }
    }

    fun filter(path: ElementPath): List<ElementPath> {
        return paths.filter { item ->
            path.indices.none { item.indices[it] != path.indices[it] }
        }
    }
}

