package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.gui.UI
import kotlinx.coroutines.experimental.launch

class TaskAsync(val callback: suspend ((ITask) -> Unit) -> Unit) : ITask {

    override fun run(state: Program) {
        launch(UI) {
            try {

                callback {
                    state.taskHistory.processTask(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}