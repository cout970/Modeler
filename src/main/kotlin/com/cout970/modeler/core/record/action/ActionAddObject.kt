package com.cout970.modeler.core.record.action

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.IObject
import com.cout970.modeler.controller.ModelTransformer

/**
 * Created by cout970 on 2017/06/09.
 */
class ActionAddObject(val transformer: ModelTransformer, val model: IModel, val obj: IObject) : IAction {

    override fun run() {
        transformer.model = model.transformObjects { it + obj }
    }

    override fun undo() {
        transformer.model = model
    }
}