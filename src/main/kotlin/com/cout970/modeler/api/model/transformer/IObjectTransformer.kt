package com.cout970.modeler.api.model.transformer

import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/07/09.
 */
interface IObjectTransformer {

    fun translate(obj: IObject, translation: IVector3): IObject
    fun rotate(obj: IObject, pivot: IVector3, rot: IQuaternion): IObject
    fun scale(obj: IObject, center: IVector3, axis: IVector3, offset: Float): IObject

    fun translateTexture(obj: IObject, translation: IVector2): IObject
    fun rotateTexture(obj: IObject, center: IVector2, angle: Double): IObject
    fun scaleTexture(obj: IObject, center: IVector2, axis: IVector2, offset: Float): IObject
}