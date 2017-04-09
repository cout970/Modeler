package com.cout970.modeler.newView.viewtarget

import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.newView.selector.ISelectable

/**
 * Created by cout970 on 2017/04/08.
 */
abstract class ViewTarget(val modelEditor: ModelEditor) {

    abstract val selectableObjects: List<ISelectable>
    var hoveredObject: ISelectable? = null
    var selectedObject: ISelectable? = null

    abstract val is3d: Boolean

    var tmpModel: Model? = null

    fun getModel(): Model = tmpModel ?: modelEditor.model
}