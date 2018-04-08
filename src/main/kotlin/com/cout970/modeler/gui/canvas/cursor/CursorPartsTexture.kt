package com.cout970.modeler.gui.canvas.cursor

import com.cout970.collision.IPolygon
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.collision.toTexturePolygon
import com.cout970.modeler.core.helpers.PickupHelper
import com.cout970.modeler.core.helpers.TransformationHelper
import com.cout970.modeler.gui.canvas.IRotable
import com.cout970.modeler.gui.canvas.IScalable
import com.cout970.modeler.gui.canvas.ITranslatable
import com.cout970.modeler.gui.canvas.helpers.CanvasHelper
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.util.toDegrees
import com.cout970.modeler.util.toVector2
import com.cout970.raytrace.IRayObstacle
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

class CursorPartTranslateTexture(
        val cursor: Cursor,
        val parameters: CursorParameters,
        override val translationAxis: IVector3
) : ITranslatable {

    override val polygons: List<IPolygon> = calculatePolygons()

    override val hitbox: IRayObstacle? = null

    fun calculatePolygons(): List<IPolygon> {
        val start = (cursor.center - Vector3.ONE * parameters.width).toVector2()
        val end = (cursor.center + translationAxis * parameters.length + Vector3.ONE * parameters.width).toVector2()
        return listOf((start to end).toTexturePolygon())
    }

    override fun applyTranslation(offset: Float, selection: ISelection, model: IModel, material: IMaterial): IModel {
        val dir = translationAxis.toVector2() * vec2Of(1, -1) / material.size
        return TransformationHelper.translateTexture(model, selection, dir * offset)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CursorPartTranslateTexture) return false

        if (translationAxis != other.translationAxis) return false

        return true
    }

    override fun hashCode(): Int {
        return translationAxis.hashCode()
    }
}

class CursorPartRotateTexture(
        val cursor: Cursor,
        parameters: CursorParameters
) : IRotable {

    override val tangent: IVector3 = Vector3.Z_AXIS
    override val center: IVector3 get() = cursor.center
    override val polygons: List<IPolygon> = RenderUtil.createCirclePolygons(center.toVector2(), parameters.length,
            parameters.width)

    override val hitbox: IRayObstacle? = null

    override fun applyRotation(offset: Float, selection: ISelection, model: IModel, material: IMaterial): IModel {
        val center2 = PickupHelper.fromCanvasToMaterial(center.toVector2(), material)
        return TransformationHelper.rotateTexture(model, selection, center2, -offset.toDegrees())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CursorPartRotateTexture) return false

        if (cursor != other.cursor) return false

        return true
    }

    override fun hashCode(): Int {
        return cursor.hashCode()
    }
}

class CursorPartScaleTexture(
        val cursor: Cursor,
        val parameters: CursorParameters,
        override val scaleAxis: IVector3
) : IScalable {

    override val polygons: List<IPolygon> = calculatePolygons()
    override val center: IVector3 get() = cursor.center

    override val hitbox: IRayObstacle? = null

    fun calculatePolygons(): List<IPolygon> {
        val start = (cursor.center - Vector3.ONE * parameters.width).toVector2()
        val end = (cursor.center + scaleAxis * parameters.length + Vector3.ONE * parameters.width).toVector2()
        return listOf((start to end).toTexturePolygon())
    }

    override fun applyScale(offset: Float, selection: ISelection, model: IModel, material: IMaterial): IModel {
        val dir = scaleAxis.toVector2() / material.size
        val center2 = PickupHelper.fromCanvasToMaterial(center.toVector2(), material)
        return TransformationHelper.scaleTexture(model, selection, center2, dir, offset)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CursorPartScaleTexture) return false

        if (scaleAxis != other.scaleAxis) return false

        return true
    }

    override fun hashCode(): Int {
        return scaleAxis.hashCode()
    }
}