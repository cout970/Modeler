package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef

/**
 * Created by cout970 on 2017/07/20.
 */

class TaskUpdateMaterial(
        val ref: IMaterialRef,
        val oldMaterial: IMaterial,
        val newMaterial: IMaterial
) : IUndoableTask {

    override fun run(state: ProgramState) {
        state.projectManager.updateMaterial(ref, newMaterial)
        state.projectManager.loadedMaterials.forEach { it.loadTexture(state.resourceLoader) }
        state.gui.editorPanel.rightPanelModule.presenter.updateObjectList()
    }

    override fun undo(state: ProgramState) {
        state.projectManager.updateMaterial(ref, oldMaterial)
        state.projectManager.loadedMaterials.forEach { it.loadTexture(state.resourceLoader) }
        state.gui.editorPanel.rightPanelModule.presenter.updateObjectList()
    }
}