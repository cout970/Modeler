package com.cout970.modeler.util

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.ModelGroup
import com.cout970.modeler.model.Quad
import com.cout970.modeler.modeleditor.selection.ISelection
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.div
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2017/02/04.
 */
fun Quad.center3D(): IVector3 {
    val ab = (b.pos + a.pos) / 2
    val cd = (d.pos + c.pos) / 2
    return (ab + cd) / 2
}

fun Quad.center2D(): IVector2 {
    val ab = (b.tex + a.tex) / 2
    val cd = (d.tex + c.tex) / 2
    return (ab + cd) / 2
}

fun Iterable<IVector3>.middle(): IVector3 {
    var sum: IVector3? = null
    var count = 0
    for (i in this) {
        if (sum == null) sum = i else sum += i
        count++
    }
    if (sum == null) return vec3Of(0)
    return sum / count
}

fun Iterable<IVector2>.middle(): IVector2 {
    var sum: IVector2? = null
    var count = 0
    for (i in this) {
        if (sum == null) sum = i else sum += i
        count++
    }
    if (sum == null) return vec2Of(0)
    return sum / count
}

fun Model.applyGroup(selection: ISelection, groupFunc: (ModelGroup) -> ModelGroup): Model {
    return copy(groups.replaceSelected(selection) { _, group ->
        groupFunc(group)
    })
}

fun Model.applyMesh(selection: ISelection, meshFunc: (Mesh) -> Mesh): Model {
    return copy(groups.replaceSelected(selection) { groupIndex, group ->
        group.copy(group.meshes.replaceSelected(selection, groupIndex) { _, mesh ->
            meshFunc(mesh)
        })
    })
}

fun Model.applyMeshAndGroup(selection: ISelection, meshFunc: (Mesh, ModelGroup) -> Mesh): Model {
    return copy(groups.replaceSelected(selection) { groupIndex, group ->
        group.copy(group.meshes.replaceSelected(selection, groupIndex) { _, mesh ->
            meshFunc(mesh, group)
        })
    })
}