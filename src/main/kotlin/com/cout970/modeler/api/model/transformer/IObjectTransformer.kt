package com.cout970.modeler.api.model.transformer

import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.vector.api.IVector2

/**
 * Created by cout970 on 2017/07/09.
 */
// TODO remove when the texture editing tools are finished
interface IObjectTransformer {

    fun translateTexture(obj: IObject, translation: IVector2): IObject
    fun rotateTexture(obj: IObject, center: IVector2, angle: Double): IObject
    fun scaleTexture(obj: IObject, center: IVector2, axis: IVector2, offset: Float): IObject
}