package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState
import com.cout970.modeler.view.render.tool.camera.CameraHandler

/**
 * Created by cout970 on 2017/07/20.
 */
class TaskUpdateCameraProjection(
        val handler: CameraHandler,
        val ortho: Boolean
) : ITask {

    override fun run(state: ProgramState) {
        handler.setOrtho(ortho)
    }
}