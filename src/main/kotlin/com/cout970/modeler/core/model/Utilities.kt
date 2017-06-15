package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.IObject
import com.cout970.modeler.api.model.selection.IObjectSelection

/**
 * Created by cout970 on 2017/06/09.
 */

fun Model.transform(trsTransformation: TRSTransformation): Model {
    return copy(objects = objects.map {
        it.transform { it.transform(trsTransformation) }
    })
}

fun IModel.transformObjects(sel: List<IObjectSelection>, func: (IObject) -> IObject): IModel {
    return transformObjects { objs ->
        val indexSet = sel.map { it.objectIndex }
        objs.mapIndexed { index, obj ->
            if (index in indexSet) func(obj) else obj
        }
    }
}