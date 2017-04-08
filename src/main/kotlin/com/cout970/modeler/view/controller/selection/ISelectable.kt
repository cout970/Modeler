package com.cout970.modeler.view.controller.selection

import com.cout970.modeler.model.Model
import com.cout970.raytrace.IRayObstacle
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/03/26.
 */
interface ISelectable : IRayObstacle

interface ITranslatable : ISelectable {
    val translationAxis: IVector3

    fun applyTranslation(offset: Float, model: Model): Model
}

interface IRotable : ISelectable {
    val center: IVector3
    // normal vector of the plane where this obj will be rotated
    val normal: IVector3

    fun applyRotation(offset: Float, model: Model): Model
}

interface IScalable : ISelectable {
    val scaleAxis: IVector3
    val center: IVector3

    fun applyScale(offset: Float, model: Model): Model
}