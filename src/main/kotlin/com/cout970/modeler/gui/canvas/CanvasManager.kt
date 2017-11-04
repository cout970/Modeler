package com.cout970.modeler.gui.canvas

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.controller.ITaskProcessor
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.cursor.Cursor
import com.cout970.modeler.gui.canvas.input.DragTick
import com.cout970.modeler.gui.canvas.input.nextTick
import com.cout970.modeler.util.*
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times

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
        Profiler.startSection("canvasManager")
        val canvas = gui.canvasContainer.selectedCanvas
        canvas?.let {

            val targets = cursor.getSelectableParts(gui, canvas)
            last = last.nextTick(gui, canvas, targets)

            gui.state.hoveredObject = last.hovered

            (last.hovered as? ITranslatable)?.let {
                val offset = last.step.offset
                tmpCursor = Cursor(realCursor.center + it.translationAxis * offset)
            }

            last.tmpModel.let {
                gui.state.tmpModel = it
                gui.state.modelHash = it?.hashCode() ?: 0
                gui.state.visibilityHash = it?.visibilities?.hashCode() ?: 0
            }

            last.task?.let {
                last = last.copy(task = null)
                processor.processTask(it)
                tmpCursor = null
            }
        }

        if (last.drag.dragStart == null) {
            updateSelectedCanvas()
        }
        Profiler.endSection()
    }

    fun onModelUpdate(old: IModel, new: IModel) {
        updateCursorCenter()
    }

    fun onSelectionUpdate(old: Nullable<ISelection>, new: Nullable<ISelection>) {
        updateCursorCenter()
    }

    fun updateSelectedCanvas() {
        val mousePos = gui.input.mouse.getMousePos()
        gui.canvasContainer.canvas.forEach { canvas ->
            if (mousePos.isInside(canvas.absolutePositionV, canvas.size.toIVector())) {
                gui.canvasContainer.selectedCanvas = canvas
            }
        }
    }

    fun updateCursorCenter() {
        val selection = gui.modelAccessor.modelSelectionHandler.getSelection()
        selection.ifNotNull {
            val model = gui.state.tmpModel ?: gui.modelAccessor.model

            val newCenter = when (it.selectionType) {
                SelectionType.OBJECT -> model.getSelectedObjects(it)
                        .map { it.getCenter() }
                        .middle()

                SelectionType.FACE -> it.refs
                        .filterIsInstance<IFaceRef>()
                        .map { model.getObject(ObjectRef(it.objectIndex)) to it.faceIndex }
                        .map { (obj, index) -> obj.mesh.faces[index].pos.map { obj.mesh.pos[it] }.middle() } // TODO fix java.lang.IndexOutOfBoundsException: Index: 32, Size: 32
                        .middle()

                SelectionType.EDGE -> it.refs
                        .filterIsInstance<IEdgeRef>()
                        .map { model.getObject(ObjectRef(it.objectIndex)) to it }
                        .flatMap { (obj, ref) -> listOf(obj.mesh.pos[ref.firstIndex], obj.mesh.pos[ref.secondIndex]) }
                        .middle()

                SelectionType.VERTEX -> it.refs
                        .filterIsInstance<IPosRef>()
                        .map { model.getObject(ObjectRef(it.objectIndex)).mesh.pos[it.posIndex] }
                        .middle()
            }
            if (!newCenter.hasNaN()) {
                realCursor = Cursor(newCenter)
                tmpCursor = null
            }
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

    private fun getCanvasUnderTheMouse(): Nullable<Canvas> {
        val pos = gui.input.mouse.getMousePos()
        val canvas = gui.canvasContainer.canvas
        val affectedCanvas = canvas.filter { pos.isInside(it.absolutePositionV, it.size.toIVector()) }

        return affectedCanvas.firstOrNull().asNullable()
    }

    fun selectPart(): Boolean {
        val canvas = getCanvasUnderTheMouse()
        canvas.ifNotNull {
            gui.dispatcher.onEvent("canvas.select", it)
            return true
        }
        return false
    }

    fun moveCamera(): Boolean {
        val canvas = getCanvasUnderTheMouse()
        canvas.ifNotNull {
            lastClick = if (System.currentTimeMillis() - lastClick < 500) {
                gui.dispatcher.onEvent("canvas.jump.camera", it); 0L
            } else {
                System.currentTimeMillis()
            }
            return true
        }
        return false
    }
}