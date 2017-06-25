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

    val listeners: MutableList<(ISelection?, ISelection?) -> Unit> = mutableListOf()

    var ref: List<IObjectRef> = emptyList()
        private set(value) {
            val old = getSelection()
            field = value
            lastModified = System.currentTimeMillis()
            val new = getSelection()
            listeners.forEach { it.invoke(old, new) }
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

    fun getSelection(): ISelection? {
        if (ref.isEmpty()) return null
        return Selection(SelectionTarget.MODEL, SelectionType.OBJECT, ref)
    }
}