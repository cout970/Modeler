package com.cout970.modeler.controller

import com.cout970.modeler.core.model.selection.ObjectSelection

/**
 * Created by cout970 on 2017/06/15.
 */
class SelectionHandler {

    var selection: List<ObjectSelection> = emptyList()
        private set(value) {
            field = value
            lastModified = System.currentTimeMillis()
        }
    var lastModified = 0L
        private set

    fun onSelect(first: ObjectSelection?, state: GuiState) {
        if (selection.isEmpty()) {
            if (first != null) {
                selection = listOf(first)
            }
        } else {
            if (first != null) {
                selection = listOf(first)
            } else {
                selection = listOf()
            }
        }
    }
}