package com.cout970.modeler.view.controller

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.modeler.config.Config
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.action.ActionModifyModelShape
import com.cout970.modeler.selection.VertexPosSelection
import com.cout970.modeler.selection.vertexPosSelection
import com.cout970.modeler.util.*
import com.cout970.modeler.view.scene.Scene3d
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Matrix4d
import org.joml.Vector3d

/**
 * Created by cout970 on 2016/12/17.
 */
class ModelSelector(val scene: Scene3d, val controller: SceneController, val modelEditor: ModelEditor) {

    val transformationMode get() = controller.transformationMode
    val selection get() = modelEditor.selectionManager.vertexPosSelection
    val selectionCenter: IVector3 get() = selection.center3D(modelEditor.model)
    var time: Long = 0L

    val translateCursor = TranslationCursorTracker()
    val rotateCursor = RotationCursorTracker()
    val scaleCursor = ScaleCursorTracker()

    // view and projection matrix
    var matrix = Matrix4d()
    var mouseSnapshot = MouseSnapshot(vec2Of(0), Ray(vec3Of(0), vec3Of(0)))
    var capturedMouse: MouseSnapshot? = null
    var viewportSize = vec2Of(1)

    fun update() {
        if (controller.selectedScene === scene && selection != VertexPosSelection.EMPTY) {
            controller.cursorCenter = selectionCenter + controller.selectedModelAxis.direction * translateCursor.offset
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
        if (selection != VertexPosSelection.EMPTY) {
            val tracker = when (transformationMode) {
                TransformationMode.TRANSLATION -> translateCursor
                TransformationMode.ROTATION -> rotateCursor
                TransformationMode.SCALE -> scaleCursor
            }
            // cursor not selecting an axis of the cursor
            if (capturedMouse == null) {
                //try to get the axis hovered by the cursor
                val cursorParams = scene.cursorParameters
                val cursor = Cursor(selectionCenter, tracker.transformationMode, cursorParams)
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
                    tracker.updateModel()
                } else {
                    capturedMouse = null
                    controller.selectedModelAxis = SelectionAxis.NONE
                    tracker.reset()

                    controller.tmpModel?.let {
                        modelEditor.historyRecord.doAction(ActionModifyModelShape(modelEditor, it))
                    }
                    controller.tmpModel = null
                }
            }
        } else {
            controller.hoveredModelAxis = SelectionAxis.NONE
            controller.selectedModelAxis = SelectionAxis.NONE
        }
    }

    fun getHoveredAxis(cursor: Cursor): SelectionAxis {

        val ray = mouseSnapshot.mouseRay

        val resX: RayTraceResult? = cursor.rayTrace(SelectionAxis.X, ray)
        val resY: RayTraceResult? = cursor.rayTrace(SelectionAxis.Y, ray)
        val resZ: RayTraceResult? = cursor.rayTrace(SelectionAxis.Z, ray)

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
        val dest = axis.direction

        val start = matrix.project(origin.toJoml3d(), intArrayOf(-1, -1, 2, 2), Vector3d())
        val end = matrix.project(dest.toJoml3d(), intArrayOf(-1, -1, 2, 2), Vector3d())
        return vec2Of(start.x, start.y) to vec2Of(end.x, end.y)
    }

    fun onEvent(e: EventMouseClick): Boolean {
        if (e.keyState != EnumKeyState.PRESS) return false

        if (Config.keyBindings.selectModel.check(e)) {
            val mousePos = controller.input.mouse.getMousePos()
            if (mousePos.isInside(scene.absolutePosition, scene.size.toIVector())) {
                if (controller.hoveredModelAxis == SelectionAxis.NONE && controller.selectedModelAxis == SelectionAxis.NONE) {
                    modelEditor.selectionManager.selectPos(mouseSnapshot.mouseRay,
                            controller.selectedScene.camera.zoom.toFloat(),
                            Config.keyBindings.multipleSelection.check(controller.input))
                    return true
                }
            }
        }
        if (Config.keyBindings.jumpCameraToCursor.check(e)) {
            val mousePos = controller.input.mouse.getMousePos()
            if (mousePos.isInside(scene.absolutePosition, scene.size.toIVector())) {
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

    interface ICursorTracker {
        val transformationMode: TransformationMode
        fun updateModel()
        fun reset()
    }

    inner abstract class AbstractCursorTracker : ICursorTracker {
        var offset = 0f
        var lastOffset = 0f

        override fun reset() {
            offset = 0f
            lastOffset = 0f
        }
    }

    inner class TranslationCursorTracker : AbstractCursorTracker() {
        override val transformationMode: TransformationMode = TransformationMode.TRANSLATION

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
                modelEditor.apply {
                    val axis = controller.selectedModelAxis
                    val newModel = editTool.translate(model, selection, axis.direction * offset)
                    controller.tmpModel = newModel
                }
            }
        }
    }

    inner class RotationCursorTracker : AbstractCursorTracker() {
        override val transformationMode: TransformationMode = TransformationMode.ROTATION

        override fun updateModel() {
            val func = { mouseRay: Ray ->

                val closest = getClosestPointOnLineSegment(mouseRay.start, mouseRay.end,
                        selectionCenter)
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
                modelEditor.apply {
                    val axis = controller.selectedModelAxis
                    val axisDir = when (axis) {
                        SelectionAxis.X -> SelectionAxis.Y
                        SelectionAxis.Y -> SelectionAxis.Z
                        SelectionAxis.Z -> SelectionAxis.X
                        else -> SelectionAxis.NONE
                    }
                    val rot = axisDir.direction.toVector4(offset).fromAxisAngToQuat()
                    val newModel = editTool.rotate(model, selection, selectionCenter, rot)
                    controller.tmpModel = newModel
                }
            }
        }
    }

    inner class ScaleCursorTracker : AbstractCursorTracker() {
        override val transformationMode: TransformationMode = TransformationMode.SCALE

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
                modelEditor.apply {
                    val axis = controller.selectedModelAxis
                    val newModel = editTool.scale(model, selection, selectionCenter, axis, offset)
                    controller.tmpModel = newModel
                }
            }
        }
    }
}