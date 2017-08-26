package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.render.tool.camera.CameraHandler

/**
 * Created by cout970 on 2017/07/20.
 */
class TaskUpdateCameraProjection(
        val handler: CameraHandler,
        val ortho: Boolean
) : ITask {

    override fun run(state: Program) {
        handler.setOrtho(ortho)
    }
}