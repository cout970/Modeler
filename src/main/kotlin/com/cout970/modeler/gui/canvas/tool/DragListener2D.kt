package com.cout970.modeler.gui.canvas.tool

import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.controller.dispatcher
import com.cout970.modeler.controller.tasks.TaskUpdateModel
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.helpers.PickupHelper
import com.cout970.modeler.core.model.AABB
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.util.isInside
import com.cout970.modeler.util.toVector2
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.Vector2
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.toVector3
import org.liquidengine.legui.component.Panel

class DragListener2D(val gui: Gui) : IDragListener {

    private val helper = Cursor2DTransformHelper()
    private var selectButton = false

    override fun onStart(startMousePos: IVector2) {
        selectButton = Config.keyBindings.selectModel.check(gui.input)
        helper.modelCache = null
        helper.offsetCache = Vector2.ORIGIN
    }

    fun updateSelection() {
        if (selectButton) {
            val canvas = gui.canvasManager.getCanvasUnderTheMouse()
            canvas.ifNotNull {
                gui.dispatcher.onEvent("canvas.select.texture", it)
            }
        }
    }

    override fun onTick(startMousePos: IVector2, endMousePos: IVector2) {
        val selection = gui.programState.textureSelection.getOrNull() ?: return
        val canvas = gui.canvasContainer.selectedCanvas ?: return
        if (canvas.viewMode != SelectionTarget.TEXTURE) return
        val mouse = startMousePos to endMousePos

        val model = helper.applyTransformation(gui, selection, mouse, canvas)
        gui.state.tmpModel = model
        gui.state.modelHash = model?.hashCode() ?: gui.state.modelHash
    }

    override fun onEnd(startMousePos: IVector2, endMousePos: IVector2) {
        val model = helper.modelCache
        if (model != null) {
            val task = TaskUpdateModel(oldModel = gui.programState.model, newModel = model)
            dispatcher.onEvent("run", Panel().apply { metadata["task"] = task })
        } else {
            updateSelection()
        }
        selectButton = false
        helper.modelCache = null
        helper.offsetCache = Vector2.ORIGIN
        gui.state.cursor.update(gui)
    }

    override fun onNoDrag() {
        if (gui.state.cursor.mode == CursorMode.SCALE) {
            val sel = gui.programState.textureSelection.getOrNull() ?: return
            val model = gui.state.model
            val canvas = gui.canvasManager.getCanvasUnderTheMouse().getOrNull() ?: return
            val cameraHandler = canvas.textureCamera
            val material = gui.state.selectedMaterial

            val boxes = Cursor2DTransformHelper.getBoxes(model, sel, material, cameraHandler.camera)
            val cursor = gui.state.cursor

            val clickPos = PickupHelper.getMousePosAbsolute(canvas, gui.input.mouse.getMousePos())

            cursor.scaleBoxIndex = -1

            boxes.forEachIndexed { index, scaleBox ->
                val aabb = AABB(scaleBox.pos.toVector3(0), (scaleBox.pos + scaleBox.size).toVector3(0))

                if (clickPos.isInside(aabb.min.toVector2(), (aabb.max - aabb.min).toVector2())) {
                    cursor.scaleBoxIndex = index
                }
            }
        }
    }
}