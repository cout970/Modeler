package com.cout970.modeler.view.controller

import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2016/12/27.
 */

enum class SelectionAxis(val axis: IVector3) {
    X(vec3Of(1, 0, 0)),
    Y(vec3Of(0, 1, 0)),
    Z(vec3Of(0, 0, 1)),
    NONE(vec3Of(0));

    companion object {
        val selectedValues = listOf(X, Y, Z)
    }
}

enum class TransformationMode {
    NONE, TRANSLATION, ROTATION, SCALE
}