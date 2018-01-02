package com.cout970.modeler.controller.usecases

import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.ModifyGui
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.gui.canvas.GridLines
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.withX
import com.cout970.modeler.util.withY
import com.cout970.modeler.util.withZ
import org.liquidengine.legui.component.Component
import kotlin.math.roundToInt

@Suppress("UNCHECKED_CAST")
@UseCase("grid.offset.change")
fun changeGridOffset(component: Component, gridLines: GridLines): ITask {
    return component
            .asNullable()
            .flatMap { comp ->
                val axis = comp.metadata["axis"] as? String ?: return@flatMap null
                val offset = comp.metadata["offset"] as? Float ?: return@flatMap null
                val text = comp.metadata["content"] as? String ?: return@flatMap null
                val listener = comp.metadata["listener"] as? Function0<Unit> ?: return@flatMap null
                changeGridOffsetHelper(gridLines, axis, offset, text, listener)
            }.getOr(TaskNone)
}

@Suppress("UNCHECKED_CAST")
@UseCase("grid.size.change")
fun changeGridSize(component: Component, gridLines: GridLines): ITask {
    return component
            .asNullable()
            .flatMap { comp ->
                val axis = comp.metadata["axis"] as? String ?: return@flatMap null
                val offset = comp.metadata["offset"] as? Float ?: return@flatMap null
                val text = comp.metadata["content"] as? String ?: return@flatMap null
                val listener = comp.metadata["listener"] as? Function0<Unit> ?: return@flatMap null
                changeGridSizeHelper(gridLines, axis, offset, text, listener)
            }.getOr(TaskNone)
}

private fun changeGridSizeHelper(gridLines: GridLines, axis: String, offset: Float, text: String,
                                   listener: () -> Unit): ITask? {
    val oldValue = when (axis) {
        "x" -> gridLines.gridSize.xf
        "y" -> gridLines.gridSize.yf
        else -> gridLines.gridSize.zf
    }
    val newValue = (getValue(text, oldValue) + offset).roundToInt()

    val vec = when (axis) {
        "x" -> gridLines.gridSize.withX(newValue)
        "y" -> gridLines.gridSize.withY(newValue)
        else -> gridLines.gridSize.withZ(newValue)
    }

    return ModifyGui { it.gridLines.gridSize = vec; listener() }
}

private fun changeGridOffsetHelper(gridLines: GridLines, axis: String, offset: Float, text: String,
                                   listener: () -> Unit): ITask? {
    val oldValue = when (axis) {
        "x" -> gridLines.gridOffset.xf
        "y" -> gridLines.gridOffset.yf
        else -> gridLines.gridOffset.zf
    }
    val newValue = getValue(text, oldValue) + offset

    val vec = when (axis) {
        "x" -> gridLines.gridOffset.withX(newValue)
        "y" -> gridLines.gridOffset.withY(newValue)
        else -> gridLines.gridOffset.withZ(newValue)
    }

    return ModifyGui { it.gridLines.gridOffset = vec; listener() }
}

private fun getValue(input: String, default: Float): Float {
    return try {
        (scriptEngine.eval(input) as? Number)?.toFloat() ?: default
    } catch (e: Exception) {
        default
    }
}