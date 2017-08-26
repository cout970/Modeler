package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState
import com.cout970.modeler.view.canvas.TransformationMode

/**
 * Created by cout970 on 2017/08/20.
 */
class TaskUpdateCursorMode(val newMode: TransformationMode) : ITask {

    override fun run(state: ProgramState) {
        state.gui.state.transformationMode = newMode
    }
}