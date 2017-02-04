package com.cout970.modeler.util

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.ModelGroup
import com.cout970.modeler.modeleditor.selection.ISelection
import com.cout970.modeler.modeleditor.selection.ModelPath

/**
 * Created by cout970 on 2016/12/09.
 */

fun <T> Iterable<T>.replace(predicate: (T) -> Boolean, transform: (T) -> T): List<T> {
    val list = mutableListOf<T>()
    for (i in this) {
        list += if (predicate(i)) transform(i) else i
    }
    return list
}

inline fun <T, R> Iterable<T>.flatMapIndexed(transform: (index: Int, T) -> Iterable<R>): List<R> {
    val destination = mutableListOf<R>()
    var index = 0
    for (element in this) {
        val list = transform(index, element)
        destination.addAll(list)
        index++
    }
    return destination
}

inline fun <T> Iterable<T>.filterNotIndexed(predicate: (index: Int, T) -> Boolean): List<T> {
    val destination = mutableListOf<T>()
    var index = 0
    for (element in this) {
        if (!predicate(index, element)) {
            destination.add(element)
        }
        index++
    }
    return destination
}

inline fun List<ModelGroup>.replaceSelected(sel: ISelection,
                                            func: (groupIndex: Int, ModelGroup) -> ModelGroup): List<ModelGroup> {
    return this.mapIndexed { index, modelGroup ->
        if (sel.containsSelectedElements(ModelPath(index))) {
            func(index, modelGroup)
        } else {
            modelGroup
        }
    }
}

inline fun List<Mesh>.replaceSelected(sel: ISelection, groupIndex: Int,
                                      func: (groupIndex: Int, Mesh) -> Mesh): List<Mesh> {
    return this.mapIndexed { index, mesh ->
        if (sel.containsSelectedElements(ModelPath(groupIndex, index))) {
            func(index, mesh)
        } else {
            mesh
        }
    }
}