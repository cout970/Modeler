package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.gui.canvas.TransformationMode

/**
 * Created by cout970 on 2017/08/20.
 */
class TaskUpdateCursorMode(val newMode: TransformationMode) : ITask {

    override fun run(state: Program) {
        state.gui.state.transformationMode = newMode
    }
}