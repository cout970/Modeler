package com.cout970.modeler.core.model

/**
 * Created by cout970 on 2017/06/09.
 */

fun Model.transform(trsTransformation: TRSTransformation): Model {
    return copy(objects = objects.map {
        it.transform { it.transform(trsTransformation) }
    })
}