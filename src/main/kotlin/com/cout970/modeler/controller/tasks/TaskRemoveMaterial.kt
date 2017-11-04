package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.core.model.material.MaterialRef

class TaskRemoveMaterial(val material: IMaterial) : IUndoableTask {

    override fun run(state: Program) {
        val index = state.projectManager.loadedMaterials.indexOf(material)
        if (index >= 0) {
            state.projectManager.removeMaterial(MaterialRef(index))
        }
    }

    override fun undo(state: Program) {
        state.projectManager.loadMaterial(material)
    }
}