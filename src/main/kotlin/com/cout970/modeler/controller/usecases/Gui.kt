package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.ModifyGui
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.CanvasManager
import com.cout970.modeler.gui.canvas.cursor.CursorManager
import com.cout970.modeler.gui.canvas.tool.CursorMode
import com.cout970.modeler.gui.canvas.tool.CursorOrientation
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

@UseCase("show.search.panel")
private fun showSearchPanel(): ITask = ModifyGui { gui ->
    gui.listeners.runGuiCommand("showSearch", mapOf("ctx" to gui.root.context))
    gui.root.reRender()
}

@UseCase("cursor.set.mode.translate")
private fun setCursorModeTranslation(): ITask = ModifyGui { it.state.cursor.mode = CursorMode.TRANSLATION }

@UseCase("cursor.set.mode.rotate")
private fun setCursorModeRotation(): ITask = ModifyGui { it.state.cursor.mode = CursorMode.ROTATION }

@UseCase("cursor.set.mode.scale")
private fun setCursorModeScale(): ITask = ModifyGui { it.state.cursor.mode = CursorMode.SCALE }

@UseCase("cursor.set.orientation.local")
private fun setCursorOrientationLocal(): ITask = ModifyGui {
    it.state.cursor.orientation = CursorOrientation.LOCAL
    it.state.cursor.update(it)
    it.listeners.runGuiCommand("updateCursorOrientation")
}

@UseCase("cursor.set.orientation.global")
private fun setCursorOrientationGlobal(): ITask = ModifyGui {
    it.state.cursor.orientation = CursorOrientation.GLOBAL
    it.state.cursor.update(it)
    it.listeners.runGuiCommand("updateCursorOrientation")
}

@UseCase("camera.move.to.cursor")
private fun moveCameraToCursor(canvasManager: CanvasManager, cursorManager: CursorManager, gui: Gui): ITask {
    canvasManager.getCanvasUnderTheMouse().ifNotNull { canvas ->
        val center = if (canvas.viewMode == SelectionTarget.TEXTURE) {
            cursorManager.textureCursor?.center ?: return@ifNotNull
        } else {
            gui.state.cursor.position
        }
        return ModifyGui { canvas.cameraHandler.setPosition(-center) }
    }
    return TaskNone
}