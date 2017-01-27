package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.vector.extensions.vec2Of

/**
 * Created by cout970 on 2016/12/09.
 */
data class ActionCreatePlane(val modelEditor: ModelEditor) : IAction {

    val model = modelEditor.model
    val plane = Mesh.createPlane(vec2Of(16))

    override fun run() {
        modelEditor.inserter.insertMesh(plane.translate(modelEditor.inserter.insertPosition))
    }

    override fun undo() {
        modelEditor.updateModel(model)
    }

    override fun toString(): String {
        return "ActionCreatePlane(modelController=$modelEditor, model=$model, cube=$plane)"
    }
}