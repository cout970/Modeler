package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.*
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.core.model.`object`.ObjectNone
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.core.model.selection.ObjectRefNone
import com.cout970.modeler.util.toAxisRotations

/**
 * Created by cout970 on 2017/06/09.
 */

val ISelection.pos: List<IPosRef> get() = refs.filterIsInstance<IPosRef>()
val ISelection.edges: List<IEdgeRef> get() = refs.filterIsInstance<IEdgeRef>()
val ISelection.faces: List<IFaceRef> get() = refs.filterIsInstance<IFaceRef>()
val ISelection.objects: List<IObjectRef> get() = refs.filterIsInstance<IObjectRef>()

val IObjectCube.pos get() = transformation.translation
val IObjectCube.rot get() = transformation.rotation
val IObjectCube.rotation get() = transformation.rotation.toAxisRotations()
val IObjectCube.size get() = transformation.scale

fun IObject.toPair(): Pair<IObjectRef, IObject> = Pair(ObjectRef(id), this)

inline val IObject.ref: IObjectRef get() = if (id == ObjectNone.id) ObjectRefNone else ObjectRef(id)
inline val IMaterial.ref: IMaterialRef get() = if (id == MaterialNone.id) MaterialRefNone else MaterialRef(id)
inline val IGroup.ref: IGroupRef get() = if (id == GroupNone.id) RootGroupRef else GroupRef(id)

fun IModel.getSelectedObjects(sel: ISelection): List<IObject> =
        sel.refs.filterIsInstance<IObjectRef>().map { getObject(it) }

fun IModel.getRecursiveChildObjects(group: IGroupRef): List<IObjectRef> {
    return getGroupObjects(group).toList() + groupTree.getChildren(group).flatMap { getRecursiveChildObjects(it) }
}

fun IModel.getRecursiveChildGroups(group: IGroupRef): List<IGroupRef> {
    val children = groupTree.getChildren(group)
    return children + children.flatMap { getRecursiveChildGroups(it) }
}