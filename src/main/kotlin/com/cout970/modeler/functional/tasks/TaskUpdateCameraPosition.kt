package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState
import com.cout970.modeler.view.canvas.Canvas
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/07/22.
 */
class TaskUpdateCameraPosition(val canvas: Canvas, val pos: IVector3) : ITask {

    override fun run(state: ProgramState) {
        canvas.cameraHandler.setPosition(pos)
    }
}