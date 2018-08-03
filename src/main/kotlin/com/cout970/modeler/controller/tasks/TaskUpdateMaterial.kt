package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.material.IMaterial

/**
 * Created by cout970 on 2017/07/20.
 */

class TaskUpdateMaterial(
        val oldMaterial: IMaterial,
        val newMaterial: IMaterial
) : IUndoableTask {

    override fun run(state: Program) {
        state.projectManager.updateMaterial(newMaterial)
        state.projectManager.loadedMaterials.forEach { it.value.loadTexture(state.resourceLoader) }
    }

    override fun undo(state: Program) {
        state.projectManager.updateMaterial(oldMaterial)
        state.projectManager.loadedMaterials.forEach { it.value.loadTexture(state.resourceLoader) }
    }
}