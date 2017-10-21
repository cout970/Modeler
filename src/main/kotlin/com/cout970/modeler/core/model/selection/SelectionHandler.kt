package com.cout970.modeler.core.model.selection

import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.combine

/**
 * Created by cout970 on 2017/10/16.
 */

class SelectionHandler(val target: SelectionTarget) {

    private val listeners: MutableList<(Nullable<ISelection>, Nullable<ISelection>) -> Unit> = mutableListOf()
    lateinit var typeGetter: () -> SelectionType
    val type: SelectionType get() = typeGetter()

    private var refs = emptySet<IRef>()
        set(value) {
            val old = getSelection()
            field = value
            lastModified = System.currentTimeMillis()
            val new = getSelection()
            listeners.forEach { it.invoke(old, new) }
        }

    var lastModified = 0L
        private set

    fun addChangeListener(func: (Nullable<ISelection>, Nullable<ISelection>) -> Unit) {
        listeners += func
    }

    fun getSelection(): Nullable<ISelection> {
        return refs.asNullable()
                .filter { it.isNotEmpty() }
                .map { Selection(target, type, it) }
    }

    fun clear() {
        refs = emptySet()
    }

    fun setSelection(selection: Nullable<ISelection>) {
        selection.filter { it.selectionTarget == target }
                .filterIsInstance<Selection>()
                .getOrNull()
                ?.let { refs = it.refs.toSet() } ?: run { refs = emptySet() }
    }

    fun isEmpty() = refs.isEmpty()

    fun updateSelection(selection: Nullable<ISelection>, multiSelection: Boolean, ref: IRef?): Nullable<ISelection> {

        if (ref == null) return if (multiSelection) selection else Nullable.castNull()

        val type = ref.getSelectionType()

        return selection
                .filter { it.selectionType == type && it.selectionTarget == target }
                .filterIsInstance<Selection>()
                .map { Selection(target, it.selectionType, it.refs.toList().combine(multiSelection, ref)) }
                .getOrCompute { Selection(target, type, setOf(ref)) }
                .asNullable()
    }
}