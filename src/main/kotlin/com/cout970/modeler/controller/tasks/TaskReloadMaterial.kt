package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.material.IMaterial

class TaskReloadMaterial(val materials: List<IMaterial>) : ITask {

    override fun run(state: Program) {
        val toRetry = materials.filter { it.loadTexture(state.resourceLoader) }

        if (toRetry.isNotEmpty()) {
            state.futureExecutor.doTask(TaskReloadMaterial(toRetry))
        }
    }
}