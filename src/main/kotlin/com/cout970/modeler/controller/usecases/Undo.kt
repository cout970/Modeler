package com.cout970.modeler.controller.usecases

import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskRedo
import com.cout970.modeler.controller.tasks.TaskUndo

/**
 * Created by cout970 on 2017/07/19.
 */

class Undo : IUseCase {
    override val key: String = "model.undo"

    override fun createTask(): ITask = TaskUndo
}

class Redo : IUseCase {
    override val key: String = "model.redo"

    override fun createTask(): ITask = TaskRedo
}