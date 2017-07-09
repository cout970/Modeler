package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.model.selection.ObjectRef

/**
 * Created by cout970 on 2017/06/09.
 */


fun IModel.transformObjects(sel: List<IObjectRef> = this.objects.mapIndexed { i, _ -> ObjectRef(i) },
                            func: (IObject) -> IObject): IModel {
    val indexSet = sel.map { it.objectIndex }
    return withObject(objects.mapIndexed { index, obj ->
        if (index in indexSet) func(obj) else obj
    })
}

fun IModel.getSelectedObjects(sel: ISelection): List<IObject> {
    return objects.mapIndexedNotNull { index, iObject ->
        if (sel.isSelected(ObjectRef(index))) iObject else null
    }
}

fun IModel.getSelectedObjectRefs(sel: ISelection): List<IObjectRef> {
    return objects.mapIndexedNotNull { index, iObject ->
        val ref = ObjectRef(index)
        if (sel.isSelected(ref)) ref else null
    }
}