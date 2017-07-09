package com.cout970.modeler.to_redo.modeleditor

import com.cout970.modeler.to_redo.model.Model

/**
 * Created by cout970 on 2017/01/20.
 */
interface IModelProvider {

    val model: Model
    val selectionManager: SelectionManager

    var modelNeedRedraw: Boolean
}