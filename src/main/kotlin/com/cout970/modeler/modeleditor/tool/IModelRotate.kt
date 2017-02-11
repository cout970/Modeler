package com.cout970.modeler.modeleditor.tool

import com.cout970.modeler.model.freemodel.FreeModel
import com.cout970.modeler.model.freemodel.Selection
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/02/07.
 */
interface IModelRotate {

    fun rotate(source: FreeModel, selection: Selection, pivot: IVector3, rotation: IQuaternion): FreeModel
}