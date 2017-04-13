package com.cout970.modeler.selection

/**
 * Created by cout970 on 2017/03/09.
 */
data class SelectionState(val element: ElementSelection, val pos: VertexPosSelection, val tex: VertexTexSelection,
                          val id: Int = selectionIds++) {

    companion object {
        // the id is used to get a different hashCode for every selection, so this can be used to detect changes
        private var selectionIds = 0
    }

    override fun hashCode(): Int {
        return selectionIds
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SelectionState) return false
        if (id != other.id) return false
        return true
    }
}