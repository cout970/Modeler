package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState
import com.cout970.modeler.api.model.material.IMaterialRef

/**
 * Created by cout970 on 2017/07/24.
 */
class TaskUpdateSelectedMaterial(val ref: IMaterialRef) : ITask {

    override fun run(state: ProgramState) {
        state.gui.state.selectedMaterial = ref
        state.gui.state.materialsHash = (System.currentTimeMillis() and 0xFFFFFFFF).toInt()
    }
}