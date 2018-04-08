package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.ModifyGui
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.gui.canvas.CanvasManager
import com.cout970.modeler.gui.canvas.TransformationMode
import com.cout970.modeler.gui.canvas.cursor.CursorManager
import com.cout970.vector.extensions.unaryMinus

/**
 * Created by cout970 on 2017/08/31.
 */

@UseCase("set.selection.type.object")
private fun setSelectionTypeObject(): ITask = ModifyGui { it.state.selectionType = SelectionType.OBJECT }

@UseCase("set.selection.type.face")
private fun setSelectionTypeFace(): ITask = ModifyGui { it.state.selectionType = SelectionType.FACE }

@UseCase("set.selection.type.edge")
private fun setSelectionTypeEdge(): ITask = ModifyGui { it.state.selectionType = SelectionType.EDGE }

@UseCase("set.selection.type.vertex")
private fun setSelectionTypeVertex(): ITask = ModifyGui { it.state.selectionType = SelectionType.VERTEX }

@UseCase("show.left.panel")
private fun showLeftPanel(): ITask = ModifyGui { gui ->
    gui.listeners.runGuiCommand("toggleLeft")
    gui.root.reRender()
}

@UseCase("show.right.panel")
private fun showRightPanel(): ITask = ModifyGui { gui ->
    gui.listeners.runGuiCommand("toggleRight")
    gui.root.reRender()
}

@UseCase("show.bottom.panel")
private fun showBottomPanel(): ITask = ModifyGui { gui ->
    gui.listeners.runGuiCommand("toggleBottom")
    gui.root.reRender()
}

@UseCase("cursor.set.mode.translate")
private fun setCursorModeTranslation(): ITask = ModifyGui { it.state.transformationMode = TransformationMode.TRANSLATION }

@UseCase("cursor.set.mode.rotate")
private fun setCursorModeRotation(): ITask = ModifyGui { it.state.transformationMode = TransformationMode.ROTATION }

@UseCase("cursor.set.mode.scale")
private fun setCursorModeScale(): ITask = ModifyGui { it.state.transformationMode = TransformationMode.SCALE }

@UseCase("camera.move.to.cursor")
private fun moveCameraToCursor(canvasManager: CanvasManager, cursorManager: CursorManager): ITask {
    canvasManager.getCanvasUnderTheMouse().ifNotNull { canvas ->
        val center = if (canvas.viewMode == SelectionTarget.TEXTURE) {
            cursorManager.textureCursor?.center ?: return@ifNotNull
        } else {
            cursorManager.modelCursor?.center ?: return@ifNotNull
        }
        return ModifyGui { canvas.cameraHandler.setPosition(-center) }
    }
    return TaskNone
}