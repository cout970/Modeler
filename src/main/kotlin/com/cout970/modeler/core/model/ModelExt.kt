package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.util.toAxisRotations

/**
 * Created by cout970 on 2017/06/09.
 */

fun IModel.getSelectedObjects(sel: ISelection): List<IObject> {
    return objects.mapIndexedNotNull { index, iObject ->
        if (sel.isSelected(ObjectRef(index))) iObject else null
    }
}

fun IModel.getSelectedObjectRefs(sel: ISelection): List<IObjectRef> {
    return objects.mapIndexedNotNull { index, _ ->
        val ref = ObjectRef(index)
        if (sel.isSelected(ref)) ref else null
    }
}

val IObjectCube.pos get() = transformation.translation
val IObjectCube.rot get() = transformation.rotation
val IObjectCube.rotation get() = transformation.rotation.toAxisRotations()
val IObjectCube.size get() = transformation.scale
