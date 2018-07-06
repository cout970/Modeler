package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.material.IMaterialRef

/**
 * Created by cout970 on 2017/07/24.
 */
class TaskUpdateMaterialSelection(val ref: IMaterialRef) : ITask {

    override fun run(state: Program) {
        state.projectManager.selectedMaterial = ref
        state.gui.state.materialsHash = (System.currentTimeMillis() and 0xFFFFFFFF).toInt()
        state.gui.root.reRender()
    }
}