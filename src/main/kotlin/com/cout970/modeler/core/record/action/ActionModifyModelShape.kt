package com.cout970.modeler.core.record.action

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.IModelSetter

/**
 * Created by cout970 on 2016/12/10.
 */
data class ActionModifyModelShape(val transformer: IModelSetter, val newModel: IModel) : IAction {

    val oldModel = transformer.model

    override fun run() {
        transformer.model = newModel
    }

    override fun undo() {
        transformer.model = oldModel
    }

    override fun toString(): String {
        return "ActionModifyModelShape(oldModel=$oldModel, newModel=$newModel)"
    }
}