package com.cout970.modeler.view.canvas.input

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.functional.tasks.ITask
import com.cout970.modeler.functional.tasks.TaskUpdateModel
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.canvas.Canvas
import com.cout970.modeler.view.canvas.ISelectable
import com.cout970.modeler.view.canvas.helpers.CanvasHelper
import com.cout970.vector.extensions.Vector2

/**
 * Created by cout970 on 2017/08/16.
 */

data class Drag(
        val dragStart: MouseState? = null,
        val mouse: MouseState = MouseState(false, Vector2.ORIGIN)
)

data class DragTick(
        val drag: Drag = Drag(null, MouseState(false, Vector2.ORIGIN)),
        val task: ITask? = null,
        val tmpModel: IModel? = null,
        val hovered: ISelectable? = null,
        val step: TransformationStep = TransformationStep()
)

fun Drag.nextDrag(mouse: MouseState): Drag {
    return if (!mouse.mousePress) {
        Drag(null, mouse)
    } else {
        Drag(dragStart ?: mouse, mouse)
    }
}

fun DragTick.nextTick(gui: Gui, canvas: Canvas, targets: List<ISelectable>): DragTick {
    val mouseState = MouseState.from(gui)

    val newDrag = drag.nextDrag(mouseState)

    val hovered = when (newDrag.dragStart == null) {
        true -> {
            val context = CanvasHelper.getMouseSpaceContext(canvas, gui.input.mouse.getMousePos())
            Hover.getHoveredObject(context, targets)
        }
        else -> hovered
    }

    val step = when (newDrag.dragStart != null && hovered != null) {
        true -> {
            val pos = newDrag.dragStart!!.mousePos to mouseState.mousePos
            step.next(gui, hovered!!, pos, canvas)
        }
        else -> TransformationStep()
    }

    val task: TaskUpdateModel?
    val newModel = step.model

    task = when (newDrag.dragStart == null && drag.dragStart != null && tmpModel != null) {
        true -> TaskUpdateModel(gui.projectManager.model, tmpModel!!)
        else -> null
    }

    return DragTick(newDrag, task, newModel, hovered, step)
}

