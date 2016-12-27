package com.cout970.modeler.modelcontrol.action

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.Transformation
import com.cout970.modeler.modelcontrol.ModelController
import com.cout970.vector.extensions.Quaternion
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2016/12/07.
 */
data class ActionCreateCube(val modelController: ModelController) : IAction {

    val model = modelController.model
    val cube = Mesh.createCube(vec3Of(1, 1, 1), transform = Transformation(modelController.inserter.insertPosition, Quaternion.IDENTITY, vec3Of(1)))

    override fun run() {
        modelController.inserter.insertComponent(cube)
        modelController.modelUpdate = true
    }

    override fun undo() {
        modelController.updateModel(model)
        modelController.modelUpdate = true
    }

    override fun toString(): String {
        return "ActionCreateCube(modelController=$modelController, model=$model, cube=$cube)"
    }
}