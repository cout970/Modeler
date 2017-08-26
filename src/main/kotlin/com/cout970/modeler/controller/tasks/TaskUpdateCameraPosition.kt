package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/07/22.
 */
class TaskUpdateCameraPosition(val canvas: Canvas, val pos: IVector3) : ITask {

    override fun run(state: Program) {
        canvas.cameraHandler.setPosition(pos)
    }
}