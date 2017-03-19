package com.cout970.modeler.util

import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.modeler.view.controller.TransformationMode
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/03/19.
 */
data class Cursor(
        val center: IVector3,
        val type: TransformationMode,
        val parameters: CursorParameters
) {

    fun rayTrace(axis: SelectionAxis, ray: Ray): RayTraceResult? {
        val (a, b) = when (type) {
            TransformationMode.TRANSLATION -> calculateTranslateHitbox(axis)
            TransformationMode.ROTATION -> calculateRotateHitbox(axis)
            TransformationMode.SCALE -> calculateScaleHitbox(axis)
        }
        return RayTraceUtil.rayTraceBox3(a, b, ray, FakeRayObstacle)
    }

    fun calculateTranslateHitbox(axis: SelectionAxis): Pair<IVector3, IVector3> {
        val radius = parameters.distanceFromCenter
        val start = radius - parameters.maxSizeOfSelectionBox / 2.0
        val end = radius + parameters.maxSizeOfSelectionBox / 2.0

        return Pair(
                center + axis.direction * start - Vector3.ONE * parameters.minSizeOfSelectionBox,
                center + axis.direction * end + Vector3.ONE * parameters.minSizeOfSelectionBox
        )
    }

    fun calculateRotateHitbox(axis: SelectionAxis): Pair<IVector3, IVector3> {
        val radius = parameters.distanceFromCenter
        val edgePoint = center + axis.direction * radius

        return Pair(
                edgePoint - axis.rotationDirection * parameters.maxSizeOfSelectionBox / 2 - Vector3.ONE * parameters.minSizeOfSelectionBox,
                edgePoint + axis.rotationDirection * parameters.maxSizeOfSelectionBox / 2 + Vector3.ONE * parameters.minSizeOfSelectionBox
        )
    }

    fun calculateScaleHitbox(axis: SelectionAxis): Pair<IVector3, IVector3> {
        return calculateTranslateHitbox(axis)
    }
}
