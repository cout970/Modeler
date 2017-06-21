package com.cout970.modeler.controller

import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.selection.Selection

/**
 * Created by cout970 on 2017/06/15.
 */
class SelectionHandler {

    var ref: List<IObjectRef> = emptyList()
        private set(value) {
            field = value
            lastModified = System.currentTimeMillis()
        }
    var lastModified = 0L
        private set

    fun onSelect(first: IObjectRef?, state: GuiState) {
        if (ref.isEmpty()) {
            if (first != null) {
                ref = listOf(first)
            }
        } else {
            if (first != null) {
                ref = listOf(first)
            } else {
                ref = listOf()
            }
        }
    }

    fun getSelection(): ISelection {
        return Selection(SelectionTarget.MODEL, SelectionType.OBJECT, ref)
    }
}