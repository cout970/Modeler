package com.cout970.modeler.view.controller

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.modeler.config.Config
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.action.ActionModifyModel
import com.cout970.modeler.modeleditor.rotate
import com.cout970.modeler.modeleditor.selection.SelectionNone
import com.cout970.modeler.modeleditor.translate
import com.cout970.modeler.util.*
import com.cout970.modeler.view.scene.SceneModel
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
class ModelSelector(val scene: SceneModel, val controller: SceneController, val modelEditor: ModelEditor) {

    val transformationMode get() = controller.modelTransformationMode
    val selection get() = modelEditor.selectionManager.modelSelection
    val selectionCenter: IVector3 get() = selection.getCenter3D(modelEditor.model)
    var time: Long = 0L

    val translateCursor = TranslationCursor()
    val rotateCursor = RotationCursor()
    val scaleCursor = TranslationCursor() //TODO

    // view and projection matrix
    var matrix = Matrix4d()
    var mouseSnapshot = MouseSnapshot(vec2Of(0), Ray(vec3Of(0), vec3Of(0)))
    var capturedMouse: MouseSnapshot? = null
    var viewportSize = vec2Of(1)

    class MouseSnapshot(val mousePos: IVector2, val mouseRay: Ray)

    fun update() {

        if (controller.selectedScene === scene && selection != SelectionNone) {
            controller.cursorCenter = selectionCenter + controller.selectedModelAxis.axis * translateCursor.offset
        }

        updateMouseRay()
    }

    fun updateMouseRay() {
        matrix = scene.getMatrixMVP().toJOML()
        val mousePos = controller.input.mouse.getMousePos() - scene.absolutePosition
        viewportSize = scene.size.toIVector()
        val viewport = intArrayOf(0, 0, viewportSize.xi, viewportSize.yi)

        val a = matrix.unproject(vec3Of(mousePos.x, viewportSize.yd - mousePos.yd, 0.0).toJoml3d(),
                viewport, Vector3d()).toIVector()
        val b = matrix.unproject(vec3Of(mousePos.x, viewportSize.yd - mousePos.yd, 1.0).toJoml3d(),
                viewport, Vector3d()).toIVector()
        val mouseRay = Ray(a, b)

        mouseSnapshot = MouseSnapshot(mousePos, mouseRay)
    }

    fun updateUserInput() {
        if (selection != SelectionNone) {
            val cursor = when (transformationMode) {
                TransformationMode.TRANSLATION -> translateCursor
                TransformationMode.ROTATION -> rotateCursor
                TransformationMode.SCALE -> scaleCursor
            }
            // cursor not selecting an axis of the cursor
            if (capturedMouse == null) {
                //try to get the axis hovered by the cursor
                controller.hoveredModelAxis = getHoveredAxis(cursor)
                //try to select an axis
                if (Config.keyBindings.selectModelControls.check(controller.input) &&
                    controller.hoveredModelAxis != SelectionAxis.NONE) {
                    //selecting the axis
                    capturedMouse = mouseSnapshot
                    controller.selectedModelAxis = controller.hoveredModelAxis
                    controller.hoveredModelAxis = SelectionAxis.NONE
                }
            } else {
                if (Config.keyBindings.selectModelControls.check(controller.input)) {
                    cursor.updateModel()
                } else {
                    capturedMouse = null
                    controller.selectedModelAxis = SelectionAxis.NONE
                    cursor.reset()

                    controller.tmpModel?.let {
                        modelEditor.historyRecord.doAction(ActionModifyModel(modelEditor, it))
                    }
                    controller.tmpModel = null
                }
            }

        } else {
            controller.hoveredModelAxis = SelectionAxis.NONE
            controller.selectedModelAxis = SelectionAxis.NONE
        }
    }

    fun getHoveredAxis(cursor: IModelCursor): SelectionAxis {
        val center = selectionCenter
        val (scale, radius, size) = getArrowProperties(scene.camera.zoom)

        val ray = mouseSnapshot.mouseRay

        val resX: RayTraceResult?
        val resY: RayTraceResult?
        val resZ: RayTraceResult?

        resX = cursor.rayTrace(SelectionAxis.X, ray, center, scale, radius, size)
        resY = cursor.rayTrace(SelectionAxis.Y, ray, center, scale, radius, size)
        resZ = cursor.rayTrace(SelectionAxis.Z, ray, center, scale, radius, size)

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

    private fun projectAxis(matrix: Matrix4d,
                            axis: SelectionAxis = controller.selectedModelAxis): Pair<IVector2, IVector2> {
        val origin = vec3Of(0)
        val dest = axis.axis

        val start = matrix.project(origin.toJoml3d(), intArrayOf(-1, -1, 2, 2), Vector3d())
        val end = matrix.project(dest.toJoml3d(), intArrayOf(-1, -1, 2, 2), Vector3d())
        return vec2Of(start.x, start.y) to vec2Of(end.x, end.y)
    }

    fun onEvent(e: EventMouseClick): Boolean {
        if (e.keyState != EnumKeyState.PRESS) return false

        if (Config.keyBindings.selectModel.check(e)) {
            if (inside(controller.input.mouse.getMousePos(), scene.absolutePosition, scene.size.toIVector())) {
                if (controller.hoveredModelAxis == SelectionAxis.NONE && controller.selectedModelAxis == SelectionAxis.NONE) {
                    modelEditor.selectionManager.mouseTrySelectModel(mouseSnapshot.mouseRay,
                            controller.selectedScene.camera.zoom.toFloat(),
                            Config.keyBindings.multipleSelection.check(controller.input))
                    return true
                }
            }
        }
        if (Config.keyBindings.jumpCameraToCursor.check(e)) {
            if (inside(controller.input.mouse.getMousePos(), scene.absolutePosition, scene.size.toIVector())) {
                if (System.currentTimeMillis() - time < 500) {
                    val hit = modelEditor.selectionManager.getMouseHit(mouseSnapshot.mouseRay)
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

    interface IModelCursor {
        val mode: TransformationMode
        fun updateModel()
        fun rayTrace(axis: SelectionAxis, ray: Ray, center: IVector3, scale: Double, radius: Double,
                     size: Double): RayTraceResult?

        fun reset()
    }

    inner class TranslationCursor : IModelCursor {
        override val mode: TransformationMode = TransformationMode.TRANSLATION
        var offset = 0f
        var lastOffset = 0f

        override fun updateModel() {
            val diff = projectAxis(matrix)
            val direction = (diff.second - diff.first)

            val oldMouse = ((capturedMouse!!.mousePos / viewportSize) * 2 - 1).run { vec2Of(x, -yd) }
            val newMouse = ((mouseSnapshot.mousePos / viewportSize) * 2 - 1).run { vec2Of(x, -yd) }

            val old = direction.project(oldMouse * viewportSize)
            val new = direction.project(newMouse * viewportSize)

            val move = (new - old) * scene.camera.zoom / Config.cursorArrowsSpeed

            if (Config.keyBindings.disableGridMotion.check(controller.input)) {
                offset = Math.round(move * 16) / 16f
            } else if (Config.keyBindings.disablePixelGridMotion.check(controller.input)) {
                offset = Math.round(move * 4) / 4f
            } else {
                offset = Math.round(move).toFloat()
            }
            if (lastOffset != offset) {
                lastOffset = offset
                controller.tmpModel = modelEditor.model.translate(selection, controller.selectedModelAxis, offset)
            }
        }

        override fun rayTrace(axis: SelectionAxis, ray: Ray, center: IVector3, scale: Double, radius: Double,
                              size: Double): RayTraceResult? {
            val start = radius - 0.2 * scale
            val end = radius + 0.2 * scale

            return when (axis) {
                SelectionAxis.X -> RayTraceUtil.rayTraceBox3(center + vec3Of(start, 0, 0) - size,
                        center + vec3Of(end, 0, 0) + size, ray, FakeRayObstacle)
                SelectionAxis.Y -> RayTraceUtil.rayTraceBox3(center + vec3Of(0, start, 0) - size,
                        center + vec3Of(0, end, 0) + size, ray, FakeRayObstacle)
                SelectionAxis.Z -> RayTraceUtil.rayTraceBox3(center + vec3Of(0, 0, start) - size,
                        center + vec3Of(0, 0, end) + size, ray, FakeRayObstacle)
                else -> null
            }
        }

        override fun reset() {
            offset = 0f
            lastOffset = 0f
        }
    }

    inner class RotationCursor : IModelCursor {
        override val mode: TransformationMode = TransformationMode.ROTATION
        var offset = 0f
        var lastOffset = 0f

        override fun updateModel() {
            val func = { mouseRay: Ray ->

                val closest = getClosestPointOnLineSegment(mouseRay.start, mouseRay.end, selectionCenter)
                val dir = (closest - selectionCenter).normalize()

                when (controller.selectedModelAxis) {
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

            if (Config.keyBindings.disableGridMotion.check(controller.input)) {
                offset = Math.round(move * 16) / 16f
            } else if (Config.keyBindings.disablePixelGridMotion.check(controller.input)) {
                offset = Math.round(move * 4) / 4f
            } else {
                offset = Math.round(move).toFloat()
            }
            offset = Math.toRadians(offset.toDouble() * 360.0 / 32).toFloat()
            if (lastOffset != offset) {
                lastOffset = offset
                controller.tmpModel = modelEditor.model.rotate(selection, controller.selectedModelAxis, offset)
            }
        }

        override fun rayTrace(axis: SelectionAxis, ray: Ray, center: IVector3, scale: Double, radius: Double,
                              size: Double): RayTraceResult? {
            return when (axis) {
                SelectionAxis.X -> RayTraceUtil.rayTraceBox3(center + vec3Of(radius, 0, -0.2 * scale) - size,
                        center + vec3Of(radius, 0, 0.2 * scale) + size, ray, FakeRayObstacle)
                SelectionAxis.Y -> RayTraceUtil.rayTraceBox3(center + vec3Of(-0.2 * scale, radius, 0) - size,
                        center + vec3Of(0.2 * scale, radius, 0) + size, ray, FakeRayObstacle)
                SelectionAxis.Z -> RayTraceUtil.rayTraceBox3(center + vec3Of(0, -0.2 * scale, radius) - size,
                        center + vec3Of(0, 0.2 * scale, radius) + size, ray, FakeRayObstacle)
                else -> null
            }
        }

        override fun reset() {
            offset = 0f
            lastOffset = 0f
        }
    }
}