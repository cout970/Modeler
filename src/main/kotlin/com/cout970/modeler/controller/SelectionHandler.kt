package com.cout970.modeler.controller

import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.view.Gui

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

    fun onSelect(first: IObjectRef?, gui: Gui) {
        if (ref.isEmpty()) {
            if (first != null) {
                ref = listOf(first)
            }
        } else {
            if (first != null) {
                if (Config.keyBindings.multipleSelection.check(gui.input)) {
                    ref += listOf(first)
                } else {
                    ref = listOf(first)
                }
            } else {
                if (!Config.keyBindings.multipleSelection.check(gui.input)) {
                    ref = listOf()
                }
            }
        }
    }

    fun getSelection(): ISelection? {
        if (ref.isEmpty()) return null
        return Selection(SelectionTarget.MODEL, SelectionType.OBJECT, ref)
    }


    fun clearSelection() {
        ref = emptyList()
    }

    fun setSelection(selection: ISelection?) {
        if (selection == null) return
        if (selection.selectionTarget == SelectionTarget.MODEL && selection.selectionType == SelectionType.OBJECT) {
            (selection as? Selection)?.let {
                ref = it.list as List<IObjectRef>
            }
        }
    }
}