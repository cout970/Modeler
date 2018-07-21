package com.cout970.modeler.controller.usecases

import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskRedo
import com.cout970.modeler.controller.tasks.TaskUndo
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/19.
 */

@UseCase("run")
private fun run(comp: Component) = comp.metadata["task"] as ITask

@UseCase("model.undo")
private fun undo() = TaskUndo

@UseCase("model.redo")
private fun redo() = TaskRedo