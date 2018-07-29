package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.gui.event.pushNotification

/**
 * Created by cout970 on 2017/07/24.
 */
class TaskImportMaterial(
        val material: IMaterial
) : IUndoableTask {

    override fun run(state: Program) {
        state.projectManager.loadMaterial(material)
        state.projectManager.loadedMaterials.forEach { it.value.loadTexture(state.resourceLoader) }
        pushNotification("Material imported", "Material '${material.name}' has been imported successfully")

    }

    override fun undo(state: Program) {
        if (state.projectManager.loadedMaterials.containsKey(material.ref)) {
            state.projectManager.removeMaterial(material.ref)
            state.projectManager.loadedMaterials.forEach { it.value.loadTexture(state.resourceLoader) }
        }
    }
}