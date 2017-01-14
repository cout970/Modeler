package com.cout970.modeler.view.controller

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.modeler.config.Config
import com.cout970.modeler.event.EventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.modeleditor.ModelController
import com.cout970.modeler.modeleditor.action.ActionTranslate
import com.cout970.modeler.modeleditor.rotate
import com.cout970.modeler.modeleditor.selection.SelectionNone
import com.cout970.modeler.modeleditor.translate
import com.cout970.modeler.util.*
import com.cout970.modeler.view.popup.Missing
import com.cout970.modeler.view.scene.ModelScene
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Matrix4d
import org.joml.Vector3d

/**
 * Created by cout970 on 2016/12/17.
 */
class ModelSelector(val scene: ModelScene, val controller: SceneController) {

    val modelController: ModelController get() = controller.modelController
    val transformationMode get() = controller.transformationMode
    val selection get() = modelController.selectionManager.selection
    val selectionCenter: IVector3 get() = selection.getCenter(controller.modelController.model)

    var offset = 0f
    var lastOffset = 0f

    // view and projection matrix
    var matrix = Matrix4d()
    var mouseSnapshot = MouseSnapshot(vec2Of(0), Ray(vec3Of(0), vec3Of(0)))
    var capturedMouse: MouseSnapshot? = null
    var viewportSize = vec2Of(1)

    class MouseSnapshot(val mousePos: IVector2, val mouseRay: Ray)

    fun updateMouseRay() {
        matrix = scene.getMatrixMVP().toJOML()
        val mousePos = controller.mouse.getMousePos() - scene.absolutePosition
        viewportSize = scene.size.toIVector()
        val viewport = intArrayOf(0, 0, viewportSize.xi, viewportSize.yi)

        val a = matrix.unproject(vec3Of(mousePos.x, viewportSize.yd - mousePos.yd, 0.0).toJoml3d(),
                viewport, Vector3d()).toIVector()
        val b = matrix.unproject(vec3Of(mousePos.x, viewportSize.yd - mousePos.yd, 1.0).toJoml3d(),
                viewport, Vector3d()).toIVector()
        val mouseRay = Ray(a, b)

        mouseSnapshot = MouseSnapshot(mousePos, mouseRay)
    }

    fun update() {

        if (controller.selectedScene === scene && selection != SelectionNone) {
            controller.cursorCenter = selectionCenter + controller.selectedAxis.axis * offset
        }

        updateMouseRay()
    }

    fun updateUserInput() {
        if (selection != SelectionNone && transformationMode != TransformationMode.NONE) {
            when (transformationMode) {
                TransformationMode.TRANSLATION -> translate()
                TransformationMode.ROTATION -> rotate()
                TransformationMode.SCALE -> {
                    controller.transformationMode = TransformationMode.NONE
                    Missing("Scale not implemented yet")
                }
                else -> {
                }
            }
        } else {
            controller.hoveredAxis = SelectionAxis.NONE
            controller.selectedAxis = SelectionAxis.NONE
        }
    }

    fun getArrowProperties(zoom: Double): Triple<Double, Double, Double> {
        val scale = zoom / 10 * Config.cursorArrowsScale
        return Triple(
                scale,
                Config.cursorArrowsDispersion * scale,
                0.0625 * scale
        )
    }

    fun getSelectedAxis(rotation: Boolean): SelectionAxis {

        val center = selectionCenter
        val (scale, radius, size) = getArrowProperties(scene.camera.zoom)
        val start = radius - 0.2 * scale
        val end = radius + 0.2 * scale

        val ray = mouseSnapshot.mouseRay

        val resX: RayTraceResult?
        val resY: RayTraceResult?
        val resZ: RayTraceResult?

        if (rotation) {
            resX = RayTraceUtil.rayTraceBox3(center + vec3Of(radius, 0, -0.2 * scale) - size,
                    center + vec3Of(radius, 0, 0.2 * scale) + size, ray, FakeRayObstacle)
            resY = RayTraceUtil.rayTraceBox3(center + vec3Of(-0.2 * scale, radius, 0) - size,
                    center + vec3Of(0.2 * scale, radius, 0) + size, ray, FakeRayObstacle)
            resZ = RayTraceUtil.rayTraceBox3(center + vec3Of(0, -0.2 * scale, radius) - size,
                    center + vec3Of(0, 0.2 * scale, radius) + size, ray, FakeRayObstacle)
        } else {
            resX = RayTraceUtil.rayTraceBox3(center + vec3Of(start, 0, 0) - size,
                    center + vec3Of(end, 0, 0) + size, ray, FakeRayObstacle)
            resY = RayTraceUtil.rayTraceBox3(center + vec3Of(0, start, 0) - size,
                    center + vec3Of(0, end, 0) + size, ray, FakeRayObstacle)
            resZ = RayTraceUtil.rayTraceBox3(center + vec3Of(0, 0, start) - size,
                    center + vec3Of(0, 0, end) + size, ray, FakeRayObstacle)
        }

        val list = mutableListOf<Pair<RayTraceResult, SelectionAxis>>()
        resX?.let { list += it to SelectionAxis.X }
        resY?.let { list += it to SelectionAxis.Y }
        resZ?.let { list += it to SelectionAxis.Z }

        if (list.isNotEmpty()) {
            list.sortBy { it.first.hit.distance(ray.start) }
            return list.first().second
        } else {
            return SelectionAxis.NONE
        }
    }

    fun translate() {
        if (capturedMouse == null) {
            controller.hoveredAxis = getSelectedAxis(false)

            //if the mouse clicks
            if (Config.keyBindings.selectModelControls.check(controller.mouse) &&
                controller.hoveredAxis != SelectionAxis.NONE) {

                capturedMouse = mouseSnapshot
                controller.selectedAxis = controller.hoveredAxis
                controller.hoveredAxis = SelectionAxis.NONE
            }
        } else {
            //if the mouse button is pressed
            if (Config.keyBindings.selectModelControls.check(controller.mouse)) {

                val diff = projectAxis(matrix)
                val direction = (diff.second - diff.first)

                val oldMouse = ((capturedMouse!!.mousePos / viewportSize) * 2 - 1).run { vec2Of(x, -yd) }
                val newMouse = ((mouseSnapshot.mousePos / viewportSize) * 2 - 1).run { vec2Of(x, -yd) }

                val old = direction.project(oldMouse * viewportSize)
                val new = direction.project(newMouse * viewportSize)

                val move = (new - old) * scene.camera.zoom / Config.cursorArrowsSpeed

                if (Config.keyBindings.disableGridMotion.check(controller.keyboard)) {
                    offset = Math.round(move * 16) / 16f
                } else if (Config.keyBindings.disablePixelGridMotion.check(controller.keyboard)) {
                    offset = Math.round(move * 4) / 4f
                } else {
                    offset = Math.round(move).toFloat()
                }
                if (lastOffset != offset) {
                    lastOffset = offset
                    controller.tmpModel = modelController.model.translate(selection, controller.selectedAxis, offset)
                }
            } else {
                capturedMouse = null
                controller.selectedAxis = SelectionAxis.NONE
                offset = 0f
                lastOffset = 0f

                controller.tmpModel?.let {
                    modelController.historyRecord.doAction(ActionTranslate(modelController, it))
                }
                controller.tmpModel = null
            }
        }
    }

    fun rotate() {
        if (capturedMouse == null) {
            controller.hoveredAxis = getSelectedAxis(true)

            //if the mouse clicks
            if (Config.keyBindings.selectModelControls.check(controller.mouse) &&
                controller.hoveredAxis != SelectionAxis.NONE) {

                capturedMouse = mouseSnapshot
                controller.selectedAxis = controller.hoveredAxis
                controller.hoveredAxis = SelectionAxis.NONE
            }
        } else {
            //if the mouse button is pressed
            if (Config.keyBindings.selectModelControls.check(controller.mouse)) {

                val func = { mouseRay: Ray ->

                    val closest = getClosestPointOnLineSegment(mouseRay.start, mouseRay.end, selectionCenter)
                    val dir = (closest - selectionCenter).normalize()

                    when (controller.selectedAxis) {
                        SelectionAxis.Z -> dir.run { Math.atan2(yd, zd) }
                        SelectionAxis.X -> dir.run { Math.atan2(-xd, zd) }
                        SelectionAxis.Y -> dir.run { Math.atan2(xd, yd) }
                        else -> 0.0
                    }
                }

                val new = with(capturedMouse!!.mouseRay, func)
                val old = with(mouseSnapshot.mouseRay, func)

                val change = new - old

                val move = Math.toDegrees(change) / 360.0 * 32 * Config.cursorRotationSpeed

                if (Config.keyBindings.disableGridMotion.check(controller.keyboard)) {
                    offset = Math.round(move * 16) / 16f
                } else if (Config.keyBindings.disablePixelGridMotion.check(controller.keyboard)) {
                    offset = Math.round(move * 4) / 4f
                } else {
                    offset = Math.round(move).toFloat()
                }
                offset = Math.toRadians(offset.toDouble() * 360.0 / 32).toFloat()
                if (lastOffset != offset) {
                    lastOffset = offset
                    controller.tmpModel = modelController.model.rotate(selection, controller.selectedAxis, offset)
                }

            } else {
                capturedMouse = null
                controller.selectedAxis = SelectionAxis.NONE
                offset = 0f
                lastOffset = 0f

                controller.tmpModel?.let {
                    modelController.historyRecord.doAction(ActionTranslate(modelController, it))
                }
                controller.tmpModel = null
            }
        }
    }

    private fun projectCenter(matrix: Matrix4d, viewport: IVector2): IVector2 {
        val point = selectionCenter
        val pos = matrix.project(point.toJoml3d(),
                intArrayOf(-1, -1, 2, 2), Vector3d())
        return vec2Of(pos.x, pos.y)
    }

    private fun projectAxis(matrix: Matrix4d): Pair<IVector2, IVector2> {
        val origin = vec3Of(0)
        val dest = controller.selectedAxis.axis

        val start = matrix.project(origin.toJoml3d(), intArrayOf(-1, -1, 2, 2), Vector3d())
        val end = matrix.project(dest.toJoml3d(), intArrayOf(-1, -1, 2, 2), Vector3d())
        return vec2Of(start.x, start.y) to vec2Of(end.x, end.y)
    }

    fun registerListeners(eventController: EventController) {
        var time: Long = 0L
        eventController.addListener(EventMouseClick::class.java, object : IEventListener<EventMouseClick> {
            override fun onEvent(e: EventMouseClick): Boolean {
                if (e.keyState != EnumKeyState.PRESS) return false

                if (Config.keyBindings.selectModel.keycode == e.button) {
                    if (inside(controller.mouse.getMousePos(), scene.absolutePosition, scene.size.toIVector())) {
                        if (controller.hoveredAxis == SelectionAxis.NONE && controller.selectedAxis == SelectionAxis.NONE) {
                            modelController.selectionManager.mouseTrySelect(mouseSnapshot.mouseRay,
                                    controller.selectedScene.camera.zoom.toFloat())
                            return true
                        }
                    }
                }
                if (Config.keyBindings.jumpCameraToCursor.keycode == e.button) {
                    if (inside(controller.mouse.getMousePos(), scene.absolutePosition, scene.size.toIVector())) {
                        if (System.currentTimeMillis() - time < 500) {
                            val hit = modelController.selectionManager.getMouseHit(mouseSnapshot.mouseRay)
                            if (hit != null) {
                                scene.camera = scene.camera.copy(position = -hit.hit)
                                return true
                            }
                        }
                        time = System.currentTimeMillis()
                    }
                }
                return false
            }
        })
    }
}