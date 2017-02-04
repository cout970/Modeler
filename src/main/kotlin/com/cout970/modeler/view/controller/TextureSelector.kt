package com.cout970.modeler.view.controller

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.modeler.config.Config
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.action.ActionModifyModel
import com.cout970.modeler.modeleditor.moveTexture
import com.cout970.modeler.modeleditor.scaleTexture
import com.cout970.modeler.modeleditor.selection.SelectionNone
import com.cout970.modeler.util.*
import com.cout970.modeler.view.scene.SceneTexture
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*
import org.joml.Matrix4d
import org.joml.Vector3d

/**
 * Created by cout970 on 2017/01/27.
 */
class TextureSelector(val scene: SceneTexture, val controller: SceneController, val modelEditor: ModelEditor) {

    val transformationMode get() = controller.textureTransformationMode
    val selection get() = modelEditor.selectionManager.textureSelection
    val selectionCenter: IVector2 get() = selection.getCenter2D(controller.tmpModel ?: modelEditor.model)

    var matrix = Matrix4d()
    var mouseSnapshot = ModelSelector.MouseSnapshot(vec2Of(0), Ray(vec3Of(0), vec3Of(0)))
    var capturedMouse: ModelSelector.MouseSnapshot? = null
    var viewportSize = vec2Of(1)

    val translateCursor = TranslationCursor()
    val rotateCursor = RotationCursor()
    val scaleCursor = ScaleCursor()

    fun update() {
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

        mouseSnapshot = ModelSelector.MouseSnapshot(mousePos, mouseRay)
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
                controller.hoveredTextureAxis = getHoveredAxis(cursor)

                //try to select an axis
                if (Config.keyBindings.selectTextureControls.check(controller.input) &&
                    controller.hoveredTextureAxis != SelectionAxis.NONE) {
                    //selecting the axis
                    capturedMouse = mouseSnapshot
                    controller.selectedTextureAxis = controller.hoveredTextureAxis
                    controller.hoveredTextureAxis = SelectionAxis.NONE
                }
            } else {
                if (Config.keyBindings.selectTextureControls.check(controller.input)) {
                    cursor.updateModel()
                } else {
                    capturedMouse = null
                    controller.selectedTextureAxis = SelectionAxis.NONE
                    cursor.reset()

                    controller.tmpModel?.let {
                        modelEditor.historyRecord.doAction(ActionModifyModel(modelEditor, it))
                    }
                    controller.tmpModel = null
                }
            }

        } else {
            controller.hoveredTextureAxis = SelectionAxis.NONE
            controller.selectedTextureAxis = SelectionAxis.NONE
        }
    }

    fun getHoveredAxis(cursor: ITextureCursor): SelectionAxis {
        val center = scene.fromTextureToWorld(selectionCenter)
        val cursorParams = CursorParameters(center, scene.camera.zoom, scene.size.toIVector())

        val ray = mouseSnapshot.mouseRay

        val resX: RayTraceResult?
        val resY: RayTraceResult?

        resX = cursor.rayTrace(SelectionAxis.X, ray, cursorParams)
        resY = cursor.rayTrace(SelectionAxis.Y, ray, cursorParams)

        val list = mutableListOf<Pair<RayTraceResult, SelectionAxis>>()
        resX?.let { list += it to SelectionAxis.X }
        resY?.let { list += it to SelectionAxis.Y }

        if (list.isNotEmpty()) {
            list.sortBy { it.first.hit.distance(ray.start) }
            return list.first().second
        } else {
            return SelectionAxis.NONE
        }
    }

    private fun projectAxis(matrix: Matrix4d, axis: SelectionAxis = controller.selectedTextureAxis)
            : Pair<IVector2, IVector2> {

        val origin = vec3Of(0)
        val dest = axis.direction

        val start = matrix.project(origin.toJoml3d(), intArrayOf(-1, -1, 2, 2), Vector3d())
        val end = matrix.project(dest.toJoml3d(), intArrayOf(-1, -1, 2, 2), Vector3d())
        return vec2Of(start.x, start.y) to vec2Of(end.x, end.y)
    }

    fun onEvent(e: EventMouseClick): Boolean {
        if (e.keyState != EnumKeyState.PRESS) return false

        if (Config.keyBindings.selectTexture.check(e)) {
            val mousePos = controller.input.mouse.getMousePos()
            if (mousePos.isInside(scene.absolutePosition, scene.size.toIVector())) {
                val selectedTextureAxis = controller.selectedTextureAxis
                val hoveredTextureAxis = controller.hoveredTextureAxis

                if (hoveredTextureAxis == SelectionAxis.NONE && selectedTextureAxis == SelectionAxis.NONE) {
                    modelEditor.selectionManager.mouseTrySelectTexture(mouseSnapshot.mouseRay,
                            controller.selectedScene.camera.zoom.toFloat(),
                            Config.keyBindings.multipleSelection.check(controller.input),
                            scene::fromTextureToWorld)
                    return true
                }
            }
        }
        return false
    }

    interface ITextureCursor {
        val mode: TransformationMode
        fun updateModel()
        fun reset()
        fun rayTrace(axis: SelectionAxis, ray: Ray, params: CursorParameters): RayTraceResult?
    }

    inner abstract class AbstractCursor : ITextureCursor {
        var offset = 0f
        var lastOffset = 0f

        override fun rayTrace(axis: SelectionAxis, ray: Ray, params: CursorParameters): RayTraceResult? {
            val center = params.center
            val radius = params.distanceFromCenter
            val start = radius - params.maxSizeOfSelectionBox / 2.0
            val end = radius + params.maxSizeOfSelectionBox / 2.0

            return RayTraceUtil.rayTraceBox3(
                    center + axis.direction * start - Vector3.ONE * params.minSizeOfSelectionBox,
                    center + axis.direction * end + Vector3.ONE * params.minSizeOfSelectionBox,
                    ray, FakeRayObstacle)
        }

        override fun reset() {
            offset = 0f
            lastOffset = 0f
        }
    }

    inner class TranslationCursor : AbstractCursor() {

        override val mode: TransformationMode = TransformationMode.TRANSLATION

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
            when (controller.selectedTextureAxis) {
                SelectionAxis.X -> offset /= modelEditor.model.groups[0].material.size.xf
                SelectionAxis.Y -> offset /= -modelEditor.model.groups[0].material.size.yf
                else -> Unit
            }
            if (lastOffset != offset) {
                lastOffset = offset
                val translation = controller.selectedTextureAxis.direction * offset
                controller.tmpModel = modelEditor.model.moveTexture(selection, translation)
            }
        }
    }

    inner class RotationCursor : AbstractCursor() {
        override val mode: TransformationMode = TransformationMode.ROTATION

        override fun updateModel() {

        }

        override fun rayTrace(axis: SelectionAxis, ray: Ray, params: CursorParameters): RayTraceResult? {
            val center = params.center
            val radius = params.distanceFromCenter
            val edgePoint = center + axis.direction * radius

            return RayTraceUtil.rayTraceBox3(
                    edgePoint + params.minSizeOfSelectionBox + axis.rotationDirection * params.minSizeOfSelectionBox / 2,
                    edgePoint - params.minSizeOfSelectionBox - axis.rotationDirection * params.minSizeOfSelectionBox / 2,
                    ray, FakeRayObstacle)
        }
    }

    inner class ScaleCursor : AbstractCursor() {
        override val mode: TransformationMode = TransformationMode.SCALE

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
            when (controller.selectedTextureAxis) {
                SelectionAxis.X -> offset /= modelEditor.model.groups[0].material.size.xf
                SelectionAxis.Y -> offset /= -modelEditor.model.groups[0].material.size.yf
                else -> Unit
            }
            if (lastOffset != offset) {
                lastOffset = offset
                val model = modelEditor.model
                val scale = Vector3.ONE + controller.selectedTextureAxis.direction * offset

                controller.tmpModel = model.scaleTexture(selection, scale)
            }
        }
    }
}