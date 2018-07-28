package com.cout970.modeler.gui.canvas.tool

import com.cout970.modeler.controller.dispatcher
import com.cout970.modeler.controller.tasks.TaskUpdateModel
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.SceneSpaceContext
import com.cout970.modeler.gui.canvas.helpers.CanvasHelper
import com.cout970.modeler.util.getClosest
import com.cout970.modeler.util.toIVector
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Panel

class DragListener3D(val gui: Gui) : IDragListener {

    private val helper = Cursor3DTransformHelper()

    override fun onNoDrag() {
        val mousePos = gui.input.mouse.getMousePos()
        val canvas = gui.canvasContainer.selectedCanvas ?: return
        val context = CanvasHelper.getMouseSpaceContext(canvas, mousePos)
        val cursor = gui.state.cursor
        val camera = canvas.cameraHandler.camera
        val viewport = canvas.size.toIVector()

        cursor.getParts().forEach { it.hovered = false }

        val targets = cursor.getParts().map { part ->
            part to part.calculateHitbox2(cursor, camera, viewport)
        }

        val part = getHoveredObject3D(context, targets) ?: return

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
        helper.cache?.let { cache ->
            val task = TaskUpdateModel(oldModel = gui.programState.model, newModel = cache)
            dispatcher.onEvent("run", Panel().apply { metadata["task"] = task })
        }
        helper.cache = null
        gui.state.cursor.update(gui)
    }

    fun <T> getHoveredObject3D(ctx: SceneSpaceContext, objs: List<Pair<T, IRayObstacle>>): T? {

        val ray = ctx.mouseRay
        val list = mutableListOf<Pair<RayTraceResult, T>>()

        objs.forEach { obj ->
            val res = obj.second.rayTrace(ray)
            res?.let { list += it to obj.first }
        }

        return list.getClosest(ray)?.second
    }
}