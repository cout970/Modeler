package com.cout970.modeler.modeleditor.tool

import com.cout970.modeler.model.freemodel.FreeModel
import com.cout970.modeler.model.freemodel.Selection
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/02/11.
 */
interface IModelScale {

    fun scale(source: FreeModel, selection: Selection, center: IVector3, axis: SelectionAxis,
              offset: Float): FreeModel
}