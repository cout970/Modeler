package com.cout970.modeler.modeleditor

import com.cout970.modeler.model.*
import com.cout970.modeler.modeleditor.action.ActionCreateCube
import com.cout970.modeler.modeleditor.action.ActionCreatePlane
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2016/12/09.
 */
class ModelInserter(val modelEditor: ModelEditor) {

    var insertPath: ElementPath = ElementPath(intArrayOf())
    var insertPosition = vec3Of(0, 0, 0)

    fun insertElement(elem: IElement) {
        modelEditor.apply {
            val newModel = model.copy(elements = insert(model.elements, elem, 0))
            updateModel(newModel)
        }
    }

    private fun insert(list: List<IElement>, elem: IElement, level: Int): List<IElement> {
        if (insertPath.indices.size == level) {
            return list + elem
        } else {
            val group = list[insertPath.indices[level]] as IElementGroup
            return insert(group.elements, elem, level + 1)
        }
    }

    fun addCube() {
        modelEditor.historyRecord.doAction(ActionCreateCube(modelEditor))
    }

    fun addPlane() {
        modelEditor.historyRecord.doAction(ActionCreatePlane(modelEditor))
    }

    // model is the current model, copiedModel is the model when ctrl+c was pressed,
    // selection is the part of the model to paste
    fun paste(model: Model, copiedModel: Model, selection: Selection): Model {
        //TODO
        return model
    }
}