package com.cout970.modeler.gui.canvas

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import org.joml.Matrix4d

/**
 * Created by cout970 on 2017/06/15.
 */

data class SceneSpaceContext(val mousePos: IVector2, val mouseRay: Ray, val mvpMatrix: Matrix4d)

data class EventMouseDrag(val oldPos: IVector2, val newPos: IVector2)

enum class TransformationMode {
    TRANSLATION, ROTATION, SCALE
}

interface ISelectable {
    val hitbox: IRayObstacle
}

interface ITranslatable : ISelectable {
    val translationAxis: IVector3

    fun applyTranslation(offset: Float, selection: ISelection, model: IModel): IModel
}

interface IRotable : ISelectable {
    val center: IVector3
    // normal vector of the plane where this obj will be rotated
    val tangent: IVector3

    fun applyRotation(offset: Float, selection: ISelection, model: IModel): IModel
}

interface IScalable : ISelectable {
    val scaleAxis: IVector3
    val center: IVector3

    fun applyScale(offset: Float, selection: ISelection, model: IModel): IModel
}