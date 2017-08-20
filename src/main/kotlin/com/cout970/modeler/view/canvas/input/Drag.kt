package com.cout970.modeler.view.canvas.input

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.functional.tasks.ITask
import com.cout970.modeler.functional.tasks.TaskUpdateModel
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.canvas.Canvas
import com.cout970.modeler.view.canvas.ISelectable
import com.cout970.modeler.view.canvas.cursor.Cursor
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
        val step: TransformationStep = TransformationStep(),
        val cursor: Cursor? = null
)

fun Drag.nextDrag(mouse: MouseState): Drag {
    return if (!mouse.mousePress) {
        Drag(null, mouse)
    } else {
        Drag(dragStart ?: mouse, mouse)
    }
}

fun DragTick.nextTick(gui: Gui, canvas: Canvas, cursor: Cursor): DragTick {
    val mouseState = MouseState.from(gui)

    val newDrag = drag.nextDrag(mouseState)

    val hovered = when (newDrag.dragStart == null) {
                      true -> Hover.getHoveredObject(gui, canvas, cursor)
                      else -> hovered
                  }

    val (step, newCursor) = when (newDrag.dragStart != null && hovered != null) {
        true -> {
            val pos = newDrag.dragStart!!.mousePos to mouseState.mousePos
            step.next(gui, hovered!!, pos, canvas, cursor)
        }
        else -> TransformationStep() to null
    }

    val task: TaskUpdateModel?
    val newModel = step.model

    task = when (newDrag.dragStart == null && drag.dragStart != null && tmpModel != null) {
        true -> TaskUpdateModel(gui.projectManager.model, tmpModel!!)
        else -> null
    }

    return DragTick(newDrag, task, newModel, hovered, step, newCursor)
}

