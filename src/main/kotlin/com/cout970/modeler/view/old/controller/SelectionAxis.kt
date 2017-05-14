package com.cout970.modeler.view.old.controller

import com.cout970.modeler.view.newView.selector.ISelectable
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2016/12/27.
 */

enum class SelectionAxis(val direction: IVector3, val rotationDirection: IVector3) : ISelectable {
    X(vec3Of(1, 0, 0), vec3Of(0, 0, 1)),
    Y(vec3Of(0, 1, 0), vec3Of(1, 0, 0)),
    Z(vec3Of(0, 0, 1), vec3Of(0, 1, 0)),
    NONE(vec3Of(0), vec3Of(0, 0, 0));

    companion object {
        val selectedValues = listOf(X, Y, Z)
    }

    override fun rayTrace(ray: Ray): RayTraceResult? {
        return null
    }
}

