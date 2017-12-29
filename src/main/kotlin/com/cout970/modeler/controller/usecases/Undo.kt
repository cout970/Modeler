package com.cout970.modeler.controller.usecases

import com.cout970.modeler.controller.tasks.TaskRedo
import com.cout970.modeler.controller.tasks.TaskUndo

/**
 * Created by cout970 on 2017/07/19.
 */

@UseCase("model.undo")
fun undo() = TaskUndo

@UseCase("model.redo")
fun redo() = TaskRedo