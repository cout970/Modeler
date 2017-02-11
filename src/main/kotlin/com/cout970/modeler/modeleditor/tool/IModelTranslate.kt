package com.cout970.modeler.modeleditor.tool

import com.cout970.modeler.model.freemodel.FreeModel
import com.cout970.modeler.model.freemodel.Selection
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/02/07.
 */
interface IModelTranslate {

    fun translate(source: FreeModel, selection: Selection, translation: IVector3): FreeModel
}