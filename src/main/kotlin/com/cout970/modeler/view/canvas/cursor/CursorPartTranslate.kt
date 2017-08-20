package com.cout970.modeler.view.canvas.cursor

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.model.getSelectedObjectRefs
import com.cout970.modeler.core.tool.EditTool
import com.cout970.modeler.util.*
import com.cout970.modeler.view.canvas.IRotable
import com.cout970.modeler.view.canvas.IScalable
import com.cout970.modeler.view.canvas.ITranslatable
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times

/**
 * Created by cout970 on 2017/07/22.
 */

class CursorPartTranslate(
        val cursor: Cursor,
        val parameters: CursorParameters,
        override val translationAxis: IVector3
) : ITranslatable {

    override val hitbox: IRayObstacle get() = AABBObstacle(this::calculateHitbox)

    fun calculateHitbox(): Pair<IVector3, IVector3> {
        return Pair(
                cursor.center - Vector3.ONE * parameters.width,
                cursor.center + translationAxis * parameters.length + Vector3.ONE * parameters.width
        )
    }

    override fun applyTranslation(offset: Float, selection: ISelection, model: IModel): IModel {
        val sel = model.getSelectedObjectRefs(selection)
        return EditTool.translate(model, sel, translationAxis * offset)
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

class CursorPartRotate(
        val cursor: Cursor,
        val parameters: CursorParameters,
        override val tangent: IVector3,
        val cotangent: IVector3
) : IRotable {

    override val center: IVector3 get() = cursor.center

    val mesh get() = RenderUtil.createCircleMesh(center, tangent, parameters.length, parameters.width)

    override val hitbox: IRayObstacle = object : IRayObstacle {
        override fun rayTrace(ray: Ray): RayTraceResult? {
            return mesh.getHits(ray).getClosest(ray)
        }
    }

    override fun applyRotation(offset: Float, selection: ISelection, model: IModel): IModel {
        val sel = model.getSelectedObjectRefs(selection)
        return EditTool.rotate(model, sel, center, quatOfAxisAngled(tangent, offset.toRads()))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CursorPartRotate) return false

        if (tangent != other.tangent) return false
        if (cotangent != other.cotangent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tangent.hashCode()
        result = 31 * result + cotangent.hashCode()
        return result
    }
}

class CursorPartScale(
        val cursor: Cursor,
        val parameters: CursorParameters,
        override val scaleAxis: IVector3
) : IScalable {

    override val center: IVector3 get() = cursor.center

    override val hitbox: IRayObstacle get() = AABBObstacle(this::calculateHitbox)

    fun calculateHitbox(): Pair<IVector3, IVector3> {
        return Pair(
                cursor.center - Vector3.ONE * parameters.width,
                cursor.center + scaleAxis * parameters.length + Vector3.ONE * parameters.width
        )
    }

    override fun applyScale(offset: Float, selection: ISelection, model: IModel): IModel {
        val sel = model.getSelectedObjectRefs(selection)
        return EditTool.scale(model, sel, center, scaleAxis, offset)
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

data class AABBObstacle(val calculateHitbox: () -> Pair<IVector3, IVector3>) : IRayObstacle {

    override fun rayTrace(ray: Ray): RayTraceResult? {
        val (a, b) = calculateHitbox()
        return RayTraceUtil.rayTraceBox3(a, b, ray, FakeRayObstacle)
    }
}