package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.view.canvas.Canvas

/**
 * Created by cout970 on 2017/07/20.
 */
class TaskUpdateCanvasViewMode(val canvas: Canvas, val viewMode: SelectionTarget) : ITask {

    override fun run(state: ProgramState) {
        canvas.viewMode = viewMode
    }
}