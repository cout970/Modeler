package com.cout970.modeler.util

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.ModelGroup
import com.cout970.modeler.modeleditor.selection.ModelPath
import com.cout970.modeler.modeleditor.selection.Selection
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
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

/**
 * http://stackoverflow.com/questions/3120357/get-closest-point-to-a-line
 * Port to C# made by N.Schilke using the code of Justin L.
 */
fun getClosestPointOnLineSegment(A: IVector3, B: IVector3, P: IVector3): IVector3 {
    val AP = P - A       //Vector from A to P
    val AB = B - A       //Vector from A to B

    val magnitudeAB = AB.lengthSq()          //Magnitude of AB vector (it's length squared)
    val ABAPproduct = AP dot AB              //The DOT product of a_to_p and a_to_b
    val distance = ABAPproduct / magnitudeAB //The normalized "distance" from a to your closest point

    //Check if P projection is over vectorAB
    if (distance < 0) {
        return A
    } else if (distance > 1) {
        return B
    } else {
        return A + AB * distance
    }
}