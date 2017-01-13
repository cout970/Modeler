package com.cout970.modeler.util

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.ModelGroup
import com.cout970.modeler.modeleditor.selection.ModelPath
import com.cout970.modeler.modeleditor.selection.Selection
import java.io.File

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

fun File.createIfNeeded(): File {
    if (!exists()) {
        createNewFile()
    }
    return this
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

inline fun List<ModelGroup>.replaceSelected(sel: Selection,
                                            func: (groupIndex: Int, ModelGroup) -> ModelGroup): List<ModelGroup> {
    return this.mapIndexed { index, modelGroup ->
        if (sel.containsSelectedElements(ModelPath(index))) {
            func(index, modelGroup)
        } else {
            modelGroup
        }
    }
}

inline fun List<Mesh>.replaceSelected(sel: Selection, groupIndex: Int,
                                      func: (groupIndex: Int, Mesh) -> Mesh): List<Mesh> {
    return this.mapIndexed { index, mesh ->
        if (sel.containsSelectedElements(ModelPath(groupIndex, index))) {
            func(index, mesh)
        } else {
            mesh
        }
    }
}

fun Model.applyGroup(selection: Selection, groupFunc: (ModelGroup) -> ModelGroup): Model {
    return copy(groups.replaceSelected(selection) { _, group ->
        groupFunc(group)
    })
}

fun Model.applyMesh(selection: Selection, meshFunc: (Mesh) -> Mesh): Model {
    return copy(groups.replaceSelected(selection) { groupIndex, group ->
        group.copy(group.meshes.replaceSelected(selection, groupIndex) { _, mesh ->
            meshFunc(mesh)
        })
    })
}