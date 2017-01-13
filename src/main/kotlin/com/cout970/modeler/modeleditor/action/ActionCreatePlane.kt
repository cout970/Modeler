package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.modeleditor.ModelController
import com.cout970.vector.extensions.vec2Of

/**
 * Created by cout970 on 2016/12/09.
 */
data class ActionCreatePlane(val modelController: ModelController) : IAction {

    val model = modelController.model
    val plane = Mesh.createPlane(vec2Of(1))

    override fun run() {
        modelController.inserter.insertMesh(plane.translate(modelController.inserter.insertPosition))
    }

    override fun undo() {
        modelController.updateModel(model)
    }

    override fun toString(): String {
        return "ActionCreatePlane(modelController=$modelController, model=$model, cube=$plane)"
    }
}