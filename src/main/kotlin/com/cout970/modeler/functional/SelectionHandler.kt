package com.cout970.modeler.functional

import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.util.combine
import org.funktionale.option.Option
import org.funktionale.option.orElse
import org.funktionale.option.toOption

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

    fun getSelection(): ISelection? {
        if (ref.isEmpty()) return null
        return Selection(SelectionTarget.MODEL, SelectionType.OBJECT, ref)
    }

    fun getModelSelection(): Option<ISelection> {
        if (ref.isEmpty()) return Option.None
        return Selection(SelectionTarget.MODEL, SelectionType.OBJECT, ref).toOption()
    }

    fun clearSelection() {
        ref = emptyList()
    }

    fun setSelection(selection: ISelection?) {
        if (selection == null) {
            ref = emptyList()
        } else {
            if (selection.selectionTarget == SelectionTarget.MODEL && selection.selectionType == SelectionType.OBJECT) {
                (selection as? Selection)?.let {
                    ref = it.list as List<IObjectRef>
                }
            }
        }
    }

    fun makeSelection(selection: Option<ISelection>, multiSelection: Boolean, ref: IObjectRef?): Option<ISelection> {
        if (ref == null) {
            if (multiSelection) {
                return selection
            } else {
                return Option.None
            }
        }
        return selection.flatMap { sel ->
            if (sel.selectionType == SelectionType.OBJECT &&
                sel.selectionTarget == SelectionTarget.MODEL && sel is Selection) {

                Selection(SelectionTarget.MODEL, SelectionType.OBJECT, sel.list.combine(multiSelection, ref)).toOption()
            } else {
                Option.None
            }
        }.orElse {
            Selection(SelectionTarget.MODEL, SelectionType.OBJECT, listOf(ref)).toOption()
        }
    }
}