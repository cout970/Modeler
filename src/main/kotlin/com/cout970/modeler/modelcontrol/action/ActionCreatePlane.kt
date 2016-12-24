package com.cout970.modeler.modelcontrol.action

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.modelcontrol.ModelController
import com.cout970.vector.extensions.vec2Of

/**
 * Created by cout970 on 2016/12/09.
 */
data class ActionCreatePlane(val modelController: ModelController) : IAction {

    val model = modelController.model
    val plane = Mesh.createPlane(vec2Of(1))

    override fun run() {
        modelController.inserter.insertComponent(plane)
        modelController.modelUpdate = true
    }

    override fun undo() {
        modelController.updateModel(model)
        modelController.modelUpdate = true
    }

    override fun toString(): String {
        return "ActionCreatePlane(modelController=$modelController, model=$model, cube=$plane)"
    }
}