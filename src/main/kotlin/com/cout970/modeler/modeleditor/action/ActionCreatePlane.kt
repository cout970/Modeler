package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.model.Meshes
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec2Of

/**
 * Created by cout970 on 2016/12/09.
 */
data class ActionCreatePlane(val modelEditor: ModelEditor) : IAction {

    val model = modelEditor.model
    val plane = Meshes.createPlane(vec2Of(16))

    override fun run() {
        modelEditor.inserter.insertElement(plane.transform { vertex ->
            vertex.transformPos { pos ->
                pos + modelEditor.inserter.insertPosition
            }
        })
    }

    override fun undo() {
        modelEditor.updateModel(model)
    }

    override fun toString(): String {
        return "ActionCreatePlane(modelController=$modelEditor, model=$model, cube=$plane)"
    }
}