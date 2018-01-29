package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.gui.event.Notification
import com.cout970.modeler.gui.event.NotificationHandler

class TaskRemoveMaterial(val ref: IMaterialRef, val material: IMaterial) : IUndoableTask {

    override fun run(state: Program) {
        if (state.projectManager.loadedMaterials.containsKey(ref)) {
            state.projectManager.removeMaterial(ref)
            NotificationHandler.push(Notification("Material removed",
                    "Material '${material.name}' has been removed successfully"))
        }
    }

    override fun undo(state: Program) {
        state.projectManager.loadMaterial(material)
    }
}