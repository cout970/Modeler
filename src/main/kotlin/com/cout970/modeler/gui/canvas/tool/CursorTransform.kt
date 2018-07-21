package com.cout970.modeler.gui.canvas.tool

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.helpers.TransformationHelper
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.gui.canvas.helpers.CanvasHelper
import com.cout970.modeler.gui.canvas.helpers.RotationHelper
import com.cout970.modeler.gui.canvas.helpers.ScaleHelper
import com.cout970.modeler.gui.canvas.helpers.TranslationHelper
import com.cout970.modeler.gui.canvas.input.Hover
import com.cout970.modeler.util.quatOfAxisAngled
import com.cout970.modeler.util.toIVector
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.times
import com.cout970.vector.extensions.unaryMinus

class DragListener(val gui: Gui) : IDragListener {

    private val helper = CursorTransformHelper()

    override fun onNoDrag() {
        val mousePos = gui.input.mouse.getMousePos()
        val canvas = gui.canvasContainer.selectedCanvas ?: return
        val context = CanvasHelper.getMouseSpaceContext(canvas, mousePos)
        val cursor = gui.state.cursor
        val camera = canvas.cameraHandler.camera
        val viewport = canvas.size.toIVector()

        cursor.getParts().forEach { it.hovered = false }

        val targets = cursor.getParts().map { part ->
            part to part.calculateHitbox(cursor, camera, viewport)
        }

        val part = Hover.getHoveredObject3D(context, targets) ?: return

        part.hovered = true
    }

    override fun onTick(startMousePos: IVector2, endMousePos: IVector2) {
        val selection = gui.programState.modelSelection.getOrNull() ?: return
        val cursor = gui.state.cursor
        val canvas = gui.canvasContainer.selectedCanvas ?: return
        val part = cursor.getParts().find { it.hovered } ?: return
        val mouse = startMousePos to endMousePos

        gui.state.tmpModel = helper.applyTransformation(gui, selection, cursor, part, mouse, canvas)
        cursor.update(gui)
    }

    override fun onEnd(startMousePos: IVector2, endMousePos: IVector2) {
        gui.state.tmpModel = null
        helper.cache?.let { cache ->
            //            run TaskUpdateModel(oldModel = gui.programState.model, newModel = cache)
        }
        helper.cache = null
        gui.state.cursor.update(gui)
    }
}

private class CursorTransformHelper {

    var cache: IModel? = null
    var offset: Float = 0f

    fun applyTransformation(gui: Gui, selection: ISelection, cursor: Cursor3D, hovered: CursorPart,
                            mouse: Pair<IVector2, IVector2>, canvas: Canvas): IModel {

        val oldModel = gui.programState.model
        val modelCache = this.cache ?: oldModel

        val newOffset = when (hovered.mode) {
            CursorMode.TRANSLATION -> {
                val context = CanvasHelper.getContext(canvas, mouse)
                TranslationHelper.getOffset(-hovered.vector, canvas, gui.input, context.first, context.second)
            }
            CursorMode.ROTATION -> {
                RotationHelper.getOffsetGlobal(cursor.position, hovered.vector, canvas, mouse, gui.input)
            }
            CursorMode.SCALE -> {
                val context = CanvasHelper.getContext(canvas, mouse)
                ScaleHelper.getOffset(-hovered.vector, canvas, gui.input, context.first, context.second)
            }
        }

        if (newOffset != offset) {
            this.offset = newOffset

            val model = when (hovered.mode) {
                CursorMode.TRANSLATION -> {
                    TransformationHelper.translate(oldModel, selection, hovered.vector * offset)
                }
                CursorMode.ROTATION -> {
                    TransformationHelper.rotate(oldModel, selection, cursor.position, quatOfAxisAngled(hovered.vector, offset))
                }
                CursorMode.SCALE -> {
                    TransformationHelper.scale(oldModel, selection, cursor.position, hovered.vector, offset)
                }
            }

            this.cache = model
            return model
        } else {
            this.cache = modelCache
            this.offset = newOffset
            return this.cache!!
        }
    }
}
