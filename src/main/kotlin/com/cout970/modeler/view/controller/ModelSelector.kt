package com.cout970.modeler.view.controller

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.modeler.config.Config
import com.cout970.modeler.event.EventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.model.Model
import com.cout970.modeler.modelcontrol.ModelController
import com.cout970.modeler.modelcontrol.action.ActionTranslate
import com.cout970.modeler.modelcontrol.selection.SelectionNone
import com.cout970.modeler.modelcontrol.translate
import com.cout970.modeler.util.*
import com.cout970.modeler.view.scene.Scene
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
class ModelSelector(val scene: Scene, val controller: SceneController) {

    val modelController: ModelController get() = controller.modelController
    val selectionCenter: IVector3 get() = modelController.selectionManager.selection.getCenter(controller.modelController.model)
    val center: IVector3 get() = selectionCenter + selectedAxis.axis * offset

    var mouseRay: Ray = Ray(vec3Of(0), vec3Of(0))
    var transformationMode = TransformationMode.ROTATION
    var selectedAxis = SelectionAxis.NONE
    var phantomSelectedAxis = SelectionAxis.NONE
    var blockMouse = false
    var blockMousePos = vec2Of(0)
    var offset = 0f
    var tmpModel: Model? = null
    var lastOffset = 0f

    fun update() {
        val matrixMVP = scene.getMatrixMVP()
        val matrix = matrixMVP.toJOML()
        val mousePos = controller.mouse.getMousePos() - scene.absolutePosition
        val viewport = scene.size.toIVector()

        val a = matrix.unproject(vec3Of(mousePos.x, viewport.yd - mousePos.yd, 0.0).toJoml3d(), intArrayOf(0, 0, viewport.xi, viewport.yi), Vector3d()).toIVector()
        val b = matrix.unproject(vec3Of(mousePos.x, viewport.yd - mousePos.yd, 1.0).toJoml3d(), intArrayOf(0, 0, viewport.xi, viewport.yi), Vector3d()).toIVector()

        mouseRay = Ray(a, b)

        if (modelController.selectionManager.selection != SelectionNone && transformationMode != TransformationMode.NONE) {

            if (!blockMouse) {
                val center = selectionCenter
                val scale = scene.camera.zoom / 10
                val start = 0.8f * scale
                val end = 1f * scale
                val size = vec3Of(0.0625) * scale

                val resX = RayTraceUtil.rayTraceBox3(center + vec3Of(start, 0, 0) - size, center + vec3Of(end, 0, 0) + size, mouseRay, FakeRayObstacle)
                val resY = RayTraceUtil.rayTraceBox3(center + vec3Of(0, start, 0) - size, center + vec3Of(0, end, 0) + size, mouseRay, FakeRayObstacle)
                val resZ = RayTraceUtil.rayTraceBox3(center + vec3Of(0, 0, start) - size, center + vec3Of(0, 0, end) + size, mouseRay, FakeRayObstacle)

                val list = mutableListOf<Pair<RayTraceResult, SelectionAxis>>()
                resX?.let { list += it to SelectionAxis.X }
                resY?.let { list += it to SelectionAxis.Y }
                resZ?.let { list += it to SelectionAxis.Z }

                if (list.isNotEmpty()) {
                    list.sortBy { it.first.hit.distance(mouseRay.start) }
                    phantomSelectedAxis = list.first().second
                } else {
                    phantomSelectedAxis = SelectionAxis.NONE
                }
                if (phantomSelectedAxis != SelectionAxis.NONE && Config.keyBindings.selectModelControls.check(controller.mouse)) {
                    blockMouse = true
                    blockMousePos = mousePos
                    selectedAxis = phantomSelectedAxis
                    phantomSelectedAxis = SelectionAxis.NONE
                }
            } else {
                if (!Config.keyBindings.selectModelControls.check(controller.mouse)) {
                    blockMouse = false
                    tmpModel?.let {
                        modelController.historyRecord.doAction(ActionTranslate(modelController, it))
                    }
                    selectedAxis = SelectionAxis.NONE
                    offset = 0f
                    lastOffset = 0f
                    tmpModel = null
                } else {
                    val diff = projectAxis(matrix)
                    val direction = (diff.second - diff.first)

                    val oldMouse = ((blockMousePos / viewport) * 2 - 1).run { vec2Of(x, -yd) }
                    val newMouse = ((mousePos / viewport) * 2 - 1).run { vec2Of(x, -yd) }

                    val old = direction.project(oldMouse * viewport)
                    val new = direction.project(newMouse * viewport)

                    if (Config.keyBindings.disableGridMotion.check(controller.keyboard)) {
                        offset = Math.round((new - old).toFloat() * (0.000625f * scene.camera.zoom.toFloat()) * 16).toFloat() / 16
                    } else if (Config.keyBindings.disablePixelGridMotion.check(controller.keyboard)) {
                        offset = Math.round((new - old).toFloat() * (0.000625f * scene.camera.zoom.toFloat()) * 4).toFloat() / 4
                    } else {
                        offset = Math.round((new - old).toFloat() * (0.000625f * scene.camera.zoom.toFloat())).toFloat()
                    }
                    if (lastOffset != offset) {
                        lastOffset = offset
                        tmpModel = modelController.model.translate(modelController.selectionManager.selection, selectedAxis, offset)
                    }
                }
            }
        } else {
            phantomSelectedAxis = SelectionAxis.NONE
            selectedAxis = SelectionAxis.NONE
            blockMouse = false
        }
    }

    private fun projectAxis(matrix: Matrix4d): Pair<IVector2, IVector2> {
        val origin = vec3Of(0)
        val dest = selectedAxis.axis

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
                        if (phantomSelectedAxis == SelectionAxis.NONE && selectedAxis == SelectionAxis.NONE) {
                            modelController.selectionManager.mouseTrySelect(mouseRay, controller.selectedScene.camera.zoom.toFloat())
                            return true
                        }
                    }
                }
                if (Config.keyBindings.jumpCameraToCursor.keycode == e.button) {
                    if (inside(controller.mouse.getMousePos(), scene.absolutePosition, scene.size.toIVector())) {
                        if (System.currentTimeMillis() - time < 500) {
                            val hit = modelController.selectionManager.getMouseHit(mouseRay)
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

    fun getModel(model: Model): Model {
        if (tmpModel != null) {
            return tmpModel!!
        }
        return model
    }
}