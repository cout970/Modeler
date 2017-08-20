package com.cout970.modeler.view.canvas

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.functional.ITaskProcessor
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.isInside
import com.cout970.modeler.util.middle
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.canvas.cursor.Cursor
import com.cout970.modeler.view.canvas.input.DragTick
import com.cout970.modeler.view.canvas.input.nextTick
import com.cout970.vector.api.IVector2
import org.funktionale.option.Option
import org.funktionale.option.firstOption
import org.funktionale.option.toOption

/**
 * Created by cout970 on 2017/07/22.
 */
class CanvasManager {

    lateinit var processor: ITaskProcessor
    lateinit var gui: Gui
    var lastClick = 0L
    var mousePress = false

    var lastMousePos: IVector2? = null
    var translationLastOffset = 0f
    var rotationLastOffset = 0f
    var scaleLastOffset = 0f

    var last: DragTick = DragTick()

    var realCursor = Cursor()
    var tmpCursor: Cursor? = null
    val cursor get() = tmpCursor ?: realCursor

    fun update() {
        val canvas = gui.canvasContainer.selectedCanvas
        canvas?.let {
            last = last.nextTick(gui, canvas, realCursor)
            gui.state.hoveredObject = last.hovered
            last.tmpModel.let {
                gui.state.tmpModel = it
                gui.state.modelHash = it?.hashCode() ?: 0
                gui.state.visibilityHash = it?.visibilities?.hashCode() ?: 0
            }
            last.cursor?.let {
                println(it)
                tmpCursor = it
            }
            last.task?.let {
                last = last.copy(task = null)
                processor.processTask(it)
            }
        }

        if (last.drag.dragStart == null) {
            updateSelectedCanvas()
        }
    }

    fun onSelectionUpdate(old: ISelection?, new: ISelection?) {
        updateCursorCenter(new.toOption())
    }

    fun updateSelectedCanvas() {
        val mousePos = gui.input.mouse.getMousePos()
        gui.canvasContainer.canvas.forEach { canvas ->
            if (mousePos.isInside(canvas.absolutePosition, canvas.size.toIVector())) {
                gui.canvasContainer.selectedCanvas = canvas
            }
        }
    }

    fun updateCursorCenter(selection: Option<ISelection>) {
        selection.forEach {
            val model = gui.state.tmpModel ?: gui.projectManager.model

            val newCenter = model.getSelectedObjects(it)
                    .map { it.getCenter() }
                    .middle()

            realCursor = Cursor(newCenter)
        }
    }

    fun onMouseClick(e: EventMouseClick): Boolean {

        if (e.keyState == EnumKeyState.PRESS) {
            when {
                Config.keyBindings.selectModelControls.check(e) -> return selectPart()
                Config.keyBindings.jumpCameraToCursor.check(e) -> return moveCamera()
            }
        }
        return false
    }

    private fun getCanvasUnderTheMouse(): Option<Canvas> {
        val pos = gui.input.mouse.getMousePos()
        val canvas = gui.canvasContainer.canvas
        val affectedCanvas = canvas.filter { pos.isInside(it.absolutePosition, it.size.toIVector()) }

        return affectedCanvas.firstOption()
    }

    fun selectPart(): Boolean {
        val canvas = getCanvasUnderTheMouse()
        canvas.forEach { gui.dispatcher.onEvent("canvas.select", it) }
        return canvas.isDefined()
    }

    fun moveCamera(): Boolean {
        val canvas = getCanvasUnderTheMouse()
        if (canvas.isDefined()) {

            lastClick = if (System.currentTimeMillis() - lastClick < 500) {
                canvas.forEach { gui.dispatcher.onEvent("canvas.jump.camera", it) }
                0L
            } else {
                System.currentTimeMillis()
            }
        }
        return canvas.isDefined()
    }
}