package com.cout970.modeler.functional.usecases

import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.functional.injection.Inject
import com.cout970.modeler.functional.tasks.ITask
import com.cout970.modeler.functional.tasks.TaskNone
import com.cout970.modeler.functional.tasks.TaskUpdateCameraProjection
import com.cout970.modeler.functional.tasks.TaskUpdateCanvasViewMode
import com.cout970.modeler.view.gui.comp.canvas.CanvasContainer

/**
 * Created by cout970 on 2017/07/20.
 */

class SwitchProjection : IUseCase {

    override val key: String = "view.switch.ortho"

    @Inject lateinit var canvasContainer: CanvasContainer

    override fun createTask(): ITask {
        canvasContainer.selectedCanvas?.cameraHandler?.let {
            return TaskUpdateCameraProjection(handler = it, ortho = it.camera.perspective)
        }
        return TaskNone
    }
}

class SetTextureMode : IUseCase {
    override val key: String = "view.set.texture.mode"

    @Inject lateinit var canvasContainer: CanvasContainer

    override fun createTask(): ITask {
        canvasContainer.selectedCanvas?.let {
            return TaskUpdateCanvasViewMode(it, SelectionTarget.TEXTURE)
        }
        return TaskNone
    }
}

class SetModelMode : IUseCase {
    override val key: String = "view.set.model.mode"

    @Inject lateinit var canvasContainer: CanvasContainer

    override fun createTask(): ITask {
        canvasContainer.selectedCanvas?.let {
            return TaskUpdateCanvasViewMode(it, SelectionTarget.MODEL)
        }
        return TaskNone
    }
}