package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.util.toAxisRotations

/**
 * Created by cout970 on 2017/06/09.
 */

fun IModel.getSelectedObjects(sel: ISelection): List<IObject> =
        sel.refs.filterIsInstance<IObjectRef>().map { getObject(it) }

val ISelection.pos : List<IPosRef> get() = refs.filterIsInstance<IPosRef>()
val ISelection.edges : List<IEdgeRef> get() = refs.filterIsInstance<IEdgeRef>()
val ISelection.faces : List<IFaceRef> get() = refs.filterIsInstance<IFaceRef>()
val ISelection.objects : List<IObjectRef> get() = refs.filterIsInstance<IObjectRef>()

val IObjectCube.pos get() = transformation.translation
val IObjectCube.rot get() = transformation.rotation
val IObjectCube.rotation get() = transformation.rotation.toAxisRotations()
val IObjectCube.size get() = transformation.scale

fun IObject.toPair(): Pair<IObjectRef, IObject> = Pair(ObjectRef(id), this)

inline val IObject.ref: IObjectRef get() = ObjectRef(id)
inline val IMaterial.ref: IMaterialRef get() = MaterialRef(id)