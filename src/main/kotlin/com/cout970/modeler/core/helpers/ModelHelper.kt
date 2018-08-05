package com.cout970.modeler.core.helpers

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.selection.IObjectRef

object ModelHelper {

    fun setObjectVisible(model: IModel, ref: IObjectRef, visible: Boolean): IModel {
        return model.modifyObjects({ it == ref }) { _, obj -> obj.withVisibility(visible) }
    }

    fun setGroupVisible(model: IModel, ref: IGroupRef, visible: Boolean): IModel {
        val newGroup = model.getGroup(ref).withVisibility(visible)
        return model.modifyGroup(newGroup)
    }
}