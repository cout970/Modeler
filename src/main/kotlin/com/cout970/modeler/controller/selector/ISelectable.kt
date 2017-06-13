package com.cout970.modeler.controller.selector

import com.cout970.modeler.api.model.IModel
import com.cout970.raytrace.IRayObstacle
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/03/26.
 */
interface ISelectable {
    val hitbox: IRayObstacle
    val translatableAxis: List<ITranslatable>
    val rotableAxis: List<IRotable>
    val scalableAxis: List<IScalable>
    val isPersistent: Boolean
}


interface ITranslatable {
    val translationAxis: IVector3

    fun applyTranslation(offset: Float, model: IModel): IModel
}

interface IRotable {
    val center: IVector3
    // normal vector of the plane where this obj will be rotated
    val normal: IVector3

    fun applyRotation(offset: Float, model: IModel): IModel
}

interface IScalable {
    val scaleAxis: IVector3
    val center: IVector3

    fun applyScale(offset: Float, model: IModel): IModel
}