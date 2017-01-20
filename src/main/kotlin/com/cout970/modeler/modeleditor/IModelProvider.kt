package com.cout970.modeler.modeleditor

import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.selection.SelectionManager

/**
 * Created by cout970 on 2017/01/20.
 */
interface IModelProvider {

    val model: Model
    val selectionManager: SelectionManager

    var modelNeedRedraw: Boolean
}