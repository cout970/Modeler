package com.cout970.modeler.newView.viewtarget

import com.cout970.modeler.model.Model
import com.cout970.modeler.view.controller.selection.ISelectable

/**
 * Created by cout970 on 2017/04/08.
 */
abstract class ViewTarget {

    abstract val selectableObjects: List<ISelectable>
    var hoveredObject: ISelectable? = null
    var selectedObject: ISelectable? = null

    var tmpModel: Model? = null
}