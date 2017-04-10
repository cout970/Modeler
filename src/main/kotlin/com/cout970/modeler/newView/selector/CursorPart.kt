package com.cout970.modeler.newView.selector

import com.cout970.modeler.model.Model
import com.cout970.modeler.util.FakeRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/04/09.
 */


abstract class CursorPart(val cursor: Cursor) : ISelectable {

    override fun rayTrace(ray: Ray): RayTraceResult? {
        val (a, b) = calculateHitbox()
        return RayTraceUtil.rayTraceBox3(a, b, ray, FakeRayObstacle)
    }

    abstract fun calculateHitbox(): Pair<IVector3, IVector3>
}

class CursorPartTranslate(cursor: Cursor, override val translationAxis: IVector3) : CursorPart(cursor), ITranslatable {

    override fun calculateHitbox(): Pair<IVector3, IVector3> {
        val radius = cursor.parameters.distanceFromCenter
        val start = radius - cursor.parameters.maxSizeOfSelectionBox / 2.0
        val end = radius + cursor.parameters.maxSizeOfSelectionBox / 2.0

        return Pair(
                cursor.center + translationAxis * start - Vector3.ONE * cursor.parameters.minSizeOfSelectionBox,
                cursor.center + translationAxis * end + Vector3.ONE * cursor.parameters.minSizeOfSelectionBox
        )
    }

    override fun applyTranslation(offset: Float, model: Model): Model {
        val selection = cursor.modelEditor.selectionManager.getSelectedVertexPos(model)
        return cursor.modelEditor.editTool.translate(model, selection, translationAxis * offset)
    }
}

class CursorPartRotation(cursor: Cursor, override val normal: IVector3, val axis: IVector3) : CursorPart(
        cursor), IRotable {

    override val center: IVector3 get() = cursor.center

    override fun calculateHitbox(): Pair<IVector3, IVector3> {
        val radius = cursor.parameters.distanceFromCenter
        val edgePoint = center + axis * radius

        return Pair(
                edgePoint - normal * cursor.parameters.maxSizeOfSelectionBox / 2 - Vector3.ONE * cursor.parameters.minSizeOfSelectionBox,
                edgePoint + normal * cursor.parameters.maxSizeOfSelectionBox / 2 + Vector3.ONE * cursor.parameters.minSizeOfSelectionBox
        )
    }

    override fun applyRotation(offset: Float, model: Model): Model {
        val quat = normal.toVector4(offset).fromAxisAngToQuat()
        val selection = cursor.modelEditor.selectionManager.getSelectedVertexPos(model)
        return cursor.modelEditor.editTool.rotate(model, selection, center, quat)
    }
}

class CursorPartScale(cursor: Cursor, override val scaleAxis: IVector3) : CursorPart(cursor), IScalable {

    override val center: IVector3 get() = cursor.center

    override fun calculateHitbox(): Pair<IVector3, IVector3> {
        val radius = cursor.parameters.distanceFromCenter
        val start = radius - cursor.parameters.maxSizeOfSelectionBox / 2.0
        val end = radius + cursor.parameters.maxSizeOfSelectionBox / 2.0

        return Pair(
                cursor.center + scaleAxis * start - Vector3.ONE * cursor.parameters.minSizeOfSelectionBox,
                cursor.center + scaleAxis * end + Vector3.ONE * cursor.parameters.minSizeOfSelectionBox
        )
    }

    override fun applyScale(offset: Float, model: Model): Model {
        val selection = cursor.modelEditor.selectionManager.getSelectedVertexPos(model)
        return cursor.modelEditor.editTool.scale(model, selection, center, scaleAxis, offset)
    }
}

