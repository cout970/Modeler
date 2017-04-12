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

abstract class CursorPart(val cursor: Cursor, val color: IVector3) : ISelectable {

    override fun rayTrace(ray: Ray): RayTraceResult? {
        val (a, b) = calculateHitbox()
        return RayTraceUtil.rayTraceBox3(a, b, ray, FakeRayObstacle)
    }

    abstract fun calculateHitbox(): Pair<IVector3, IVector3>
}

class CursorPartTranslate(cursor: Cursor, color: IVector3, override val translationAxis: IVector3)
    : CursorPart(cursor, color), ITranslatable {

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
        cursor.scene.tmpCursorCenter?.let { center ->
            cursor.center = center + translationAxis * offset
        }
        return cursor.modelEditor.editTool.translate(model, selection, translationAxis * offset)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CursorPartTranslate) return false

        if (translationAxis != other.translationAxis) return false

        return true
    }

    override fun hashCode(): Int {
        return translationAxis.hashCode()
    }
}

class CursorPartRotation(cursor: Cursor, color: IVector3, val axis: IVector3, override val normal: IVector3)
    : CursorPart(cursor, color), IRotable {

    override val center: IVector3 get() = cursor.center
    val coaxis: IVector3 = axis cross normal

    override fun calculateHitbox(): Pair<IVector3, IVector3> {
        val radius = cursor.parameters.distanceFromCenter
        val edgePoint = center + axis * radius

        return Pair(
                edgePoint - coaxis * cursor.parameters.maxSizeOfSelectionBox / 2 - Vector3.ONE * cursor.parameters.minSizeOfSelectionBox,
                edgePoint + coaxis * cursor.parameters.maxSizeOfSelectionBox / 2 + Vector3.ONE * cursor.parameters.minSizeOfSelectionBox
        )
    }

    override fun applyRotation(offset: Float, model: Model): Model {
        val quat = normal.toVector4(offset).fromAxisAngToQuat()
        val selection = cursor.modelEditor.selectionManager.getSelectedVertexPos(model)
        return cursor.modelEditor.editTool.rotate(model, selection, center, quat)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CursorPartRotation) return false

        if (axis != other.axis) return false

        return true
    }

    override fun hashCode(): Int {
        return axis.hashCode()
    }
}

class CursorPartScale(cursor: Cursor, color: IVector3, override val scaleAxis: IVector3)
    : CursorPart(cursor, color), IScalable {

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CursorPartScale) return false

        if (scaleAxis != other.scaleAxis) return false

        return true
    }

    override fun hashCode(): Int {
        return scaleAxis.hashCode()
    }
}


