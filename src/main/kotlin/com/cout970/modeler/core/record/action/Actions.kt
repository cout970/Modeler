package com.cout970.modeler.core.record.action

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.controller.IModelSetter

/**
 * Created by cout970 on 2017/06/21.
 */
class ActionDelete(transformer: IModelSetter, newModel: IModel) : ActionUpdateModel(transformer, newModel)

class ActionAddObject(transformer: IModelSetter, model: IModel, obj: IObject)
    : ActionUpdateModel(transformer, model.addObjects(listOf(obj)))

class ActionChangeObject(transformer: IModelSetter, newModel: IModel) : ActionUpdateModel(transformer, newModel)

class ActionPaste(transformer: IModelSetter, newModel: IModel) : ActionUpdateModel(transformer, newModel)

class ActionUpdateVisibility(transformer: IModelSetter, newModel: IModel) : ActionUpdateModel(transformer, newModel)

open class ActionUpdateModel(val transformer: IModelSetter, val newModel: IModel) : IAction {

    val oldModel = transformer.model

    override fun run() {
        transformer.model = newModel
    }

    override fun undo() {
        transformer.model = oldModel
    }
}