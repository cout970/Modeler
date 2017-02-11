package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.model.ElementObject
import com.cout970.modeler.model.Meshes
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2016/12/07.
 */
data class ActionCreateCube(val modelEditor: ModelEditor) : IAction {

    val model = modelEditor.model
    val cube: ElementObject = Meshes.createCube(vec3Of(8, 8, 8))

    override fun run() {
        modelEditor.inserter.insertElement(cube.transform { vertex ->
            vertex.transformPos { pos ->
                pos + modelEditor.inserter.insertPosition
            }
        })
    }

    override fun undo() {
        modelEditor.updateModel(model)
    }

    override fun toString(): String {
        return "ActionCreateCube(modelController=$modelEditor, model=$model, cube=$cube)"
    }
}