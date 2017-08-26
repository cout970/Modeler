package com.cout970.modeler.controller.usecases

import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskUpdateCursorMode
import com.cout970.modeler.gui.canvas.TransformationMode

/**
 * Created by cout970 on 2017/08/20.
 */

class TranslationCursorMode : IUseCase {

    override val key: String = "cursor.set.mode.translate"

    override fun createTask(): ITask {
        return TaskUpdateCursorMode(TransformationMode.TRANSLATION)
    }
}

class RotationCursorMode : IUseCase {

    override val key: String = "cursor.set.mode.rotate"

    override fun createTask(): ITask {
        return TaskUpdateCursorMode(TransformationMode.ROTATION)
    }
}

class ScaleCursorMode : IUseCase {

    override val key: String = "cursor.set.mode.scale"

    override fun createTask(): ITask {
        return TaskUpdateCursorMode(TransformationMode.SCALE)
    }
}