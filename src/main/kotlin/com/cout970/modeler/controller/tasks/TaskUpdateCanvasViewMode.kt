package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.gui.canvas.Canvas

/**
 * Created by cout970 on 2017/07/20.
 */
class TaskUpdateCanvasViewMode(val canvas: Canvas, val viewMode: SelectionTarget) : ITask {

    override fun run(state: Program) {
        canvas.viewMode = viewMode
    }
}