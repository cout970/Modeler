package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.modeleditor.ModelController
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2016/12/07.
 */
data class ActionCreateCube(val modelController: ModelController) : IAction {

    val model = modelController.model
    val cube = Mesh.createCube(vec3Of(1, 1, 1))

    override fun run() {
        modelController.inserter.insertMesh(cube.translate(modelController.inserter.insertPosition))
    }

    override fun undo() {
        modelController.updateModel(model)
    }

    override fun toString(): String {
        return "ActionCreateCube(modelController=$modelController, model=$model, cube=$cube)"
    }
}